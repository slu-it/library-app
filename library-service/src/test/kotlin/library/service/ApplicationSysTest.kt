package library.service

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import library.service.api.books.BookResource
import library.service.persistence.books.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resources
import org.springframework.hateoas.hal.Jackson2HalModule
import utils.SystemTest
import java.net.URL

@SystemTest
internal class ApplicationSysTest {

    val objectMapper = ObjectMapper().apply {
        registerModule(Jackson2HalModule())
        configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @LocalServerPort
    var port: Int = 8080

    @Autowired
    lateinit var bookRepository: BookRepository

    @BeforeEach fun setupRestAssured() {
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

        assertThat(createdBook.isbn).isEqualTo("9780553573404")
        assertThat(createdBook.title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
        assertThat(createdBook.borrowed).isNull()

        assertThat(createdBook.getLink("self")).isNotNull()
        assertThat(createdBook.getLink("borrow")).isNotNull()
        assertThat(createdBook.getLink("return")).isNull()

        // step 2: delete the book
        val bookLink = createdBook.getLink("self")
        deleteBookExpecting(bookLink, 204)
        deleteBookExpecting(bookLink, 404)

    }

    @Test fun `adding a book, borrowing it and then returning it`() {

        // step 1: create the book
        val createBookRequest = """ {
                "isbn": "9780553573404",
                "title": "A Game of Thrones: A Song of Ice and Fire (1)"
        } """
        val createdBook = createBook(createBookRequest)

        assertThat(createdBook.isbn).isEqualTo("9780553573404")
        assertThat(createdBook.title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
        assertThat(createdBook.borrowed).isNull()

        assertThat(createdBook.getLink("self")).isNotNull()
        assertThat(createdBook.getLink("borrow")).isNotNull()
        assertThat(createdBook.getLink("return")).isNull()

        // step 2: borrow the book
        val borrowLink = createdBook.getLink("borrow")
        val borrowBookRequest = """ {
            "borrower": "Rob Stark"
        } """
        val borrowedBook = borrowBook(borrowLink, borrowBookRequest)

        assertThat(borrowedBook.isbn).isEqualTo("9780553573404")
        assertThat(borrowedBook.title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
        assertThat(borrowedBook.borrowed).isNotNull()
        assertThat(borrowedBook.borrowed!!.by).isEqualTo("Rob Stark")
        assertThat(borrowedBook.borrowed!!.on).isNotNull()

        assertThat(borrowedBook.getLink("self")).isNotNull()
        assertThat(borrowedBook.getLink("borrow")).isNull()
        assertThat(borrowedBook.getLink("return")).isNotNull()

        // step 3: return the book
        val returnLink = borrowedBook.getLink("return")
        val returnedBook = returnBook(returnLink)

        assertThat(returnedBook.isbn).isEqualTo("9780553573404")
        assertThat(returnedBook.title).isEqualTo("A Game of Thrones: A Song of Ice and Fire (1)")
        assertThat(returnedBook.borrowed).isNull()

        assertThat(returnedBook.getLink("self")).isNotNull()
        assertThat(returnedBook.getLink("borrow")).isNotNull()
        assertThat(returnedBook.getLink("return")).isNull()

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
        val response = given()
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
        return objectMapper.readValue(response, BookResource::class.java)
    }

    private fun deleteBookExpecting(bookLink: Link, expectedStatus: Int) {
        // @formatter:off
            given()
                .header("Content-Type", "application/json")
            .`when`()
                .delete(toUrl(bookLink))
            .then()
                .statusCode(expectedStatus)
        // @formatter:on
    }

    private fun borrowBook(borrowLink: Link, requestBody: String): BookResource {
        // @formatter:off
        val response = given()
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
        return objectMapper.readValue(response, BookResource::class.java)
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
        val response = given()
                .header("Content-Type", "application/json")
            .`when`()
                .post(toUrl(returnLink))
            .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
            .and()
                .extract().body().asString()
        // @formatter:on
        return objectMapper.readValue(response, BookResource::class.java)
    }

    private fun returnBookExpecting(returnLink: Link, expectedStatus: Int) {
        // @formatter:off
            given()
                .header("Content-Type", "application/json")
            .`when`()
                .post(toUrl(returnLink))
            .then()
                .statusCode(expectedStatus)
        // @formatter:on
    }

    private fun getAllBooks(): BookListResource {
        // @formatter:off
        val response = given()
                .header("Content-Type", "application/json")
            .`when`()
                .get("/api/books")
            .then()
                .statusCode(200)
                .contentType("application/hal+json;charset=UTF-8")
            .and()
                .and().log().everything()
                .extract().body().asString()
        // @formatter:on
        return objectMapper.readValue(response, BookListResource::class.java)
    }

    private fun toUrl(link: Link) = URL(link.href)

    open class BookListResource : Resources<BookResource>()

}