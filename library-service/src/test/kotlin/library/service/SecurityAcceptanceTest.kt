package library.service

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import library.service.api.books.BookResource
import library.service.business.books.BookDataStore
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.persistence.books.BookRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.hateoas.Link
import org.springframework.hateoas.Resources
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.classification.AcceptanceTest
import utils.extensions.UseDockerToRunMongoDB
import utils.extensions.UseDockerToRunRabbitMQ
import java.net.URL
import java.time.OffsetDateTime

@AcceptanceTest
@UseDockerToRunMongoDB
@UseDockerToRunRabbitMQ
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class SecurityAcceptanceTest {

    val book = Book(Isbn13("9780553573404"), Title("A Game of Thrones: A Song of Ice and Fire (1)"))

    @LocalServerPort
    var port: Int = 8080

    @Autowired lateinit var bookRepository: BookRepository
    @Autowired lateinit var bookDataStore: BookDataStore

    @BeforeEach fun setupRestAssured() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @AfterEach fun deleteAllBooks() {
        bookRepository.deleteAll()
    }

    @Test fun `actuator info endpoint can be accessed by anyone`() {
        given { auth().none() } `when` { get("/actuator/info") } then { statusCode(200) }
    }

    @Test fun `actuator health endpoint can be accessed by anyone`() {
        given { auth().none() } `when` { get("/actuator/health") } then { statusCode(200) }
    }

    @ValueSource(strings = ["beans", "conditions", "configprops", "env", "loggers",
        "metrics", "scheduledtasks", "trace", "mappings"])
    @ParameterizedTest fun `any other actuator endpoint can only be accessed by an admin`(endpoint: String) {
        given { auth().none() }
                .`when` { get("/actuator/$endpoint") }
                .then { statusCode(401) }
        given { auth().basic("user", "user") }
                .`when` { get("/actuator/$endpoint") }
                .then { statusCode(403) }
        given { auth().basic("curator", "curator") }
                .`when` { get("/actuator/$endpoint") }
                .then { statusCode(403) }
        given { auth().basic("admin", "admin") }
                .`when` { get("/actuator/$endpoint") }
                .then { statusCode(200) }
    }

    @ValueSource(strings = ["/api/books", "/api/books/some-id", "/api/books/some-id/borrow", "/api/books/some-id/return"])
    @ParameterizedTest fun `API endpoints cant be accessed anonymously`(endpoint: String) {
        given { auth().none() } `when` { get(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { post(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { put(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { delete(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { head(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { options(endpoint) } then { statusCode(401) }
        given { auth().none() } `when` { patch(endpoint) } then { statusCode(401) }
    }

    @CsvSource("user, 403", "curator, 201", "admin, 201")
    @ParameterizedTest(name = "creating a book as a [{0}] will result in a [{1}] response")
    fun `books can only be created by curators and admins`(user: String, expectedStatus: Int) {
        val requestBody = """{ "isbn": "${book.isbn}", "title": "${book.title}" } """
        given {
            auth().basic(user, user)
            header("Content-Type", "application/json")
            body(requestBody)
        } `when` { post("/api/books") } then { statusCode(expectedStatus) }
    }

    @CsvSource("user, 403", "curator, 204", "admin, 204")
    @ParameterizedTest(name = "deleting a book as a [{0}] will result in a [{1}] response")
    fun `books can only be deleted by curators and admins`(user: String, expectedStatus: Int) {
        val bookId = bookDataStore.create(book).id
        given { auth().basic(user, user) } `when` { delete("/api/books/$bookId") } then { statusCode(expectedStatus) }
    }

    @CsvSource("user, 200", "curator, 200", "admin, 200")
    @ParameterizedTest(name = "borrowing a book as a [{0}] will result in a [{1}] response")
    fun `any user can borrow books`(user: String, expectedStatus: Int) {
        val bookId = bookDataStore.create(book).id
        val requestBody = """{ "borrower": "Rob Stark" }"""
        given {
            auth().basic(user, user)
            header("Content-Type", "application/json")
            body(requestBody)
        } `when` { post("/api/books/$bookId/borrow") } then { statusCode(expectedStatus) }
    }

    @CsvSource("user, 200", "curator, 200", "admin, 200")
    @ParameterizedTest(name = "returning a book as a [{0}] will result in a [{1}] response")
    fun `any user can return books`(user: String, expectedStatus: Int) {
        val bookRecord = bookDataStore.create(book).apply {
            borrow(Borrower("Rob Stark"), OffsetDateTime.now())
        }
        bookDataStore.update(bookRecord)
        given { auth().basic(user, user) } `when` { post("/api/books/${bookRecord.id}/return") } then { statusCode(expectedStatus) }
    }

    @CsvSource("user, 200", "curator, 200", "admin, 200")
    @ParameterizedTest(name = "listing all books as a [{0}] will result in a [{1}] response")
    fun `any user can list all books`(user: String, expectedStatus: Int) {
        given { auth().basic(user, user) } `when` { get("/api/books") } then { statusCode(expectedStatus) }
    }

    private fun given(body: RequestSpecification.() -> RequestSpecification): RequestSpecification {
        return body(given())
    }

    private infix fun RequestSpecification.`when`(body: RequestSpecification.() -> Response): Response {
        return body(this.`when`())
    }

    private infix fun Response.`then`(body: ValidatableResponse.() -> ValidatableResponse): ValidatableResponse {
        return body(this.then())
    }

    private fun toUrl(link: Link) = URL(link.href)

    open class BookListResource : Resources<BookResource>()

}