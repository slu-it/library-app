package library.service

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.`when`
import io.restassured.RestAssured.given
import library.service.api.books.BookResource
import library.service.database.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resources
import org.springframework.hateoas.hal.Jackson2HalModule
import utils.classification.AcceptanceTest
import utils.extensions.MongoDbExtension
import utils.extensions.RabbitMqExtension
import java.net.URL

@AcceptanceTest
@ExtendWith(MongoDbExtension::class, RabbitMqExtension::class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = [
            "application.secured=false",
            "spring.data.mongodb.port=\${MONGODB_PORT}",
            "spring.rabbitmq.port=\${RABBITMQ_PORT}"
        ]
)
internal class FunctionalAcceptanceTest(
        @Autowired val bookRepository: BookRepository
) {

    val consumerObjectMapper = ObjectMapper().apply {
        findAndRegisterModules()
        registerModule(Jackson2HalModule())
        configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @AfterEach fun deleteAllBooks() {
        bookRepository.deleteAll()
    }

    @Test fun `adding a book and then deleting it`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val createdBook = createBook(createBookRequest)

        with(createdBook) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
            assertThat(authors).isEmpty()
            assertThat(numberOfPages).isNull()
            assertThat(borrowed).isNull()

            assertThat(getLink("self")).isNotNull()
            assertThat(getLink("borrow")).isNotNull()
            assertThat(getLink("return")).isNull()
        }

        // step 2: delete the book
        val bookLink = createdBook.getLink("self")
        deleteBookExpecting(bookLink, 204)
        deleteBookExpecting(bookLink, 404)

    }

    @Test fun `adding a book and updating its data`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val bookLink = createBook(createBookRequest).getLink("self")

        // step 2: updating all updateable data
        updateBookTitle(bookLink, """ { "title": "The Updated Title" } """)
        updateBookAuthors(bookLink, """ { "authors": ["Author #1", "Author #2"] } """)
        updateBookNumberOfPages(bookLink, """ { "numberOfPages": 42 } """)

        with(getBook(bookLink)) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("The Updated Title")
            assertThat(authors).containsExactly("Author #1", "Author #2")
            assertThat(numberOfPages).isEqualTo(42)
        }

        // step 3: removing all removeable data
        removeBookAuthors(bookLink)
        removeBookNumberOfPages(bookLink)

        with(getBook(bookLink)) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("The Updated Title")
            assertThat(authors).isEmpty()
            assertThat(numberOfPages).isNull()
        }

    }

    @Test fun `adding a book, borrowing it and then returning it`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val createdBook = createBook(createBookRequest)

        with(createdBook) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
            assertThat(borrowed).isNull()

            assertThat(getLink("self")).isNotNull()
            assertThat(getLink("borrow")).isNotNull()
            assertThat(getLink("return")).isNull()
        }

        // step 2: borrow the book
        val borrowLink = createdBook.getLink("borrow")
        val borrowBookRequest = """ {
            "borrower": "Rob Stark"
        } """
        val borrowedBook = borrowBook(borrowLink, borrowBookRequest)

        with(borrowedBook) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
            assertThat(borrowed).isNotNull()
            assertThat(borrowed!!.by).isEqualTo("Rob Stark")
            assertThat(borrowed!!.on).isNotNull()

            assertThat(getLink("self")).isNotNull()
            assertThat(getLink("borrow")).isNull()
            assertThat(getLink("return")).isNotNull()
        }

        // step 3: return the book
        val returnLink = borrowedBook.getLink("return")
        val returnedBook = returnBook(returnLink)

        with(returnedBook) {
            assertThat(isbn).isEqualTo("9780553573404")
            assertThat(title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
            assertThat(borrowed).isNull()

            assertThat(getLink("self")).isNotNull()
            assertThat(getLink("borrow")).isNotNull()
            assertThat(getLink("return")).isNull()
        }

    }

    @Test fun `books can't be borrowed if already borrowed`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val createdBook = createBook(createBookRequest)

        // step 2: borrow the book twice
        val borrowLink = createdBook.getLink("borrow")
        borrowBookExpecting(borrowLink, 200)
        borrowBookExpecting(borrowLink, 409)

    }

    @Test fun `books can't be returned if already returned`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val createdBook = createBook(createBookRequest)

        // step 2: borrow the book
        val borrowLink = createdBook.getLink("borrow")
        val borrowBookRequest = """ {
            "borrower": "Arya Stark"
        } """
        val borrowedBook = borrowBook(borrowLink, borrowBookRequest)

        // step 3: return the book twice
        val returnLink = borrowedBook.getLink("return")
        returnBookExpecting(returnLink, 200)
        returnBookExpecting(returnLink, 409)

    }

    @Test fun `listing all books of the library`() {

        // step 1: create some books

        val book1 = createBook(""" {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """)
        val book2 = createBook(""" {
                "isbn": "9780553579901",
                "title": "A Clash of Kings: A Song of Ice and Fire (2)"
        } """)
        val book3 = createBook(""" {
                "isbn": "9780553573428",
                "title": "A Storm of Swords: A Song of Ice and Fire (3)"
        } """)

        // step 2: get all books

        val allBooks = getAllBooks()

        assertThat(allBooks.content)
                .hasSize(3)
                .containsOnly(book1, book2, book3)
        assertThat(allBooks.getLink("self")).isNotNull()

    }

    private fun createBook(requestBody: String): BookResource {
        // @formatter:off
        val response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .`when`()
                        .post("/api/books")
                        .then()
                        .statusCode(201)
                        .contentType("application/hal+json;charset=UTF-8")
                        .and()
                        .extract().body().asString()
        // @formatter:on
        return consumerObjectMapper.readValue(response, BookResource::class.java)
    }

    private fun deleteBookExpecting(bookLink: Link, expectedStatus: Int) {
        // @formatter:off
        `when`()
                .delete(toUrl(bookLink))
                .then()
                .statusCode(expectedStatus)
        // @formatter:on
    }

    private fun borrowBook(borrowLink: Link, requestBody: String): BookResource {
        // @formatter:off
        val response =
                given()
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .`when`()
                        .post(toUrl(borrowLink))
                        .then()
                        .statusCode(200)
                        .contentType("application/hal+json;charset=UTF-8")
                        .and()
                        .extract().body().asString()
        // @formatter:on
        return consumerObjectMapper.readValue(response, BookResource::class.java)
    }

    private fun borrowBookExpecting(borrowLink: Link, expectedStatus: Int) {
        // @formatter:off
        given()
                .header("Content-Type", "application/json")
                .body(""" { "borrower": "No One" }""")
                .`when`()
                .post(toUrl(borrowLink))
                .then()
                .statusCode(expectedStatus)
        // @formatter:on
    }

    private fun returnBook(returnLink: Link): BookResource {
        // @formatter:off
        val response =
                `when`()
                        .post(toUrl(returnLink))
                        .then()
                        .statusCode(200)
                        .contentType("application/hal+json;charset=UTF-8")
                        .and()
                        .extract().body().asString()
        // @formatter:on
        return consumerObjectMapper.readValue(response, BookResource::class.java)
    }

    private fun returnBookExpecting(returnLink: Link, expectedStatus: Int) {
        // @formatter:off
        `when`()
                .post(toUrl(returnLink))
                .then()
                .statusCode(expectedStatus)
        // @formatter:on
    }

    private fun getAllBooks(): BookListResource {
        // @formatter:off
        val response =
                `when`()
                        .get("/api/books")
                        .then()
                        .statusCode(200)
                        .contentType("application/hal+json;charset=UTF-8")
                        .and()
                        .extract().body().asString()
        // @formatter:on
        return consumerObjectMapper.readValue(response, BookListResource::class.java)
    }

    private fun getBook(bookLink: Link): BookResource {
        // @formatter:off
        val response =
                `when`()
                        .get(toUrl(bookLink))
                        .then()
                        .statusCode(200)
                        .contentType("application/hal+json;charset=UTF-8")
                        .and()
                        .extract().body().asString()
        // @formatter:on
        return consumerObjectMapper.readValue(response, BookResource::class.java)
    }

    private fun updateBookTitle(bookLink: Link, requestBody: String) {
        // @formatter:off
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .`when`()
                .put(toUrl(bookLink, "/title"))
                .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
        // @formatter:on
    }

    private fun updateBookAuthors(bookLink: Link, requestBody: String) {
        // @formatter:off
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .`when`()
                .put(toUrl(bookLink, "/authors"))
                .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
        // @formatter:on
    }

    private fun updateBookNumberOfPages(bookLink: Link, requestBody: String) {
        // @formatter:off
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .`when`()
                .put(toUrl(bookLink, "/numberOfPages"))
                .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
        // @formatter:on
    }

    private fun removeBookAuthors(bookLink: Link) {
        // @formatter:off
        `when`()
                .delete(toUrl(bookLink, "/authors"))
                .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
        // @formatter:on
    }

    private fun removeBookNumberOfPages(bookLink: Link) {
        // @formatter:off
        `when`()
                .delete(toUrl(bookLink, "/numberOfPages"))
                .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
        // @formatter:on
    }

    private fun toUrl(link: Link, postFix: String = "") = URL(link.href + postFix)

    open class BookListResource : Resources<BookResource>()

}