package library.service

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import library.service.business.books.BookCollection
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.database.BookRepository
import library.service.security.Authorizations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import utils.Books
import utils.classification.AcceptanceTest
import utils.executeAsUserWithRole
import utils.extensions.MongoDbExtension
import utils.extensions.RabbitMqExtension

@AcceptanceTest
@ExtendWith(MongoDbExtension::class, RabbitMqExtension::class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = [
            "spring.data.mongodb.port=\${MONGODB_PORT}",
            "spring.rabbitmq.port=\${RABBITMQ_PORT}"
        ]
)
internal class SecurityAcceptanceTest {

    val book = Books.THE_MARTIAN

    @Autowired lateinit var bookRepository: BookRepository
    @Autowired lateinit var bookCollection: BookCollection

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @AfterEach fun deleteAllBooks() {
        bookRepository.deleteAll()
    }

    @Nested inner class `actuator endpoints` {

        @Test fun `actuator info endpoint can be accessed by anyone`() {
            given { auth().none() } `when` { get("/actuator/info") } then { statusCode(200) }
        }

        @Test fun `actuator health endpoint can be accessed by anyone`() {
            given { auth().none() } `when` { get("/actuator/health") } then { statusCode(200) }
        }

        @ValueSource(strings = ["beans", "conditions", "configprops", "env", "loggers",
            "metrics", "scheduledtasks", "httptrace", "mappings"])
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

    }

    @Nested inner class `api endpoints` {

        @ValueSource(strings = [
            "/api/books", "/api/books/some-id",
            "/api/books/some-id/borrow", "/api/books/some-id/return",
            "/api/books/some-id/title", "/api/books/some-id/authors", "/api/books/some-id/numberOfPages"
        ])
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
        @ParameterizedTest(name = "creating a book as a {0} will result in a {1} response")
        fun `books can only be created by curators and admins`(user: String, expectedStatus: Int) {
            val requestBody = """{ "isbn": "${book.isbn}", "title": "${book.title}" } """
            given {
                auth().basic(user, user)
                header("Content-Type", "application/json")
                body(requestBody)
            } `when` { post("/api/books") } then { statusCode(expectedStatus) }
        }

        @Nested inner class `book properties can only be updated by curators and admins` {

            lateinit var bookId: BookId

            @BeforeEach fun addBook() {
                bookId = asCurator { bookCollection.addBook(book) }.id
            }

            @CsvSource("user, 403", "curator, 200", "admin, 200")
            @ParameterizedTest(name = "updating a book's authors as a {0} will result in a {1} response")
            fun `change authors property`(user: String, expectedStatus: Int) {
                val requestBody = """{ "authors": ["Foo", "Bar"] } """
                given {
                    auth().basic(user, user)
                    header("Content-Type", "application/json")
                    body(requestBody)
                } `when` { put("/api/books/$bookId/authors") } then { statusCode(expectedStatus) }
            }

            @CsvSource("user, 403", "curator, 200", "admin, 200")
            @ParameterizedTest(name = "updating a book's number of pages as a {0} will result in a {1} response")
            fun `change number of pages property`(user: String, expectedStatus: Int) {
                val requestBody = """{ "numberOfPages": 128 } """
                given {
                    auth().basic(user, user)
                    header("Content-Type", "application/json")
                    body(requestBody)
                } `when` { put("/api/books/$bookId/numberOfPages") } then { statusCode(expectedStatus) }
            }

            @CsvSource("user, 403", "curator, 200", "admin, 200")
            @ParameterizedTest(name = "updating a book's title as a {0} will result in a {1} response")
            fun `change title property`(user: String, expectedStatus: Int) {
                val requestBody = """{ "title": "Foo Bar" } """
                given {
                    auth().basic(user, user)
                    header("Content-Type", "application/json")
                    body(requestBody)
                } `when` { put("/api/books/$bookId/title") } then { statusCode(expectedStatus) }
            }

            @CsvSource("user, 403", "curator, 200", "admin, 200")
            @ParameterizedTest(name = "removing a book's authors as a {0} will result in a {1} response")
            fun `remove authors property`(user: String, expectedStatus: Int) {
                given {
                    auth().basic(user, user)
                } `when` { delete("/api/books/$bookId/authors") } then { statusCode(expectedStatus) }
            }

            @CsvSource("user, 403", "curator, 200", "admin, 200")
            @ParameterizedTest(name = "removing a book's number of pages as a {0} will result in a {1} response")
            fun `remove number of pages property`(user: String, expectedStatus: Int) {
                given {
                    auth().basic(user, user)
                } `when` { delete("/api/books/$bookId/numberOfPages") } then { statusCode(expectedStatus) }
            }

        }

        @CsvSource("user, 403", "curator, 204", "admin, 204")
        @ParameterizedTest(name = "deleting a book as a {0} will result in a {1} response")
        fun `books can only be deleted by curators and admins`(user: String, expectedStatus: Int) {
            val bookId = asCurator { bookCollection.addBook(book) }.id
            given { auth().basic(user, user) } `when` { delete("/api/books/$bookId") } then { statusCode(expectedStatus) }
        }

        @CsvSource("user, 200", "curator, 200", "admin, 200")
        @ParameterizedTest(name = "borrowing a book as a {0} will result in a {1} response")
        fun `any user can borrow books`(user: String, expectedStatus: Int) {
            val bookId = asCurator { bookCollection.addBook(book) }.id
            val requestBody = """{ "borrower": "Rob Stark" }"""
            given {
                auth().basic(user, user)
                header("Content-Type", "application/json")
                body(requestBody)
            } `when` { post("/api/books/$bookId/borrow") } then { statusCode(expectedStatus) }
        }

        @CsvSource("user, 200", "curator, 200", "admin, 200")
        @ParameterizedTest(name = "returning a book as a {0} will result in a {1} response")
        fun `any user can return books`(user: String, expectedStatus: Int) {
            val bookId = asCurator { bookCollection.addBook(book) }.id
            asUser { bookCollection.borrowBook(bookId, Borrower("Rob Stark")) }
            given { auth().basic(user, user) } `when` { post("/api/books/$bookId/return") } then { statusCode(expectedStatus) }
        }

        @CsvSource("user, 200", "curator, 200", "admin, 200")
        @ParameterizedTest(name = "listing all books as a {0} will result in a {1} response")
        fun `any user can list all books`(user: String, expectedStatus: Int) {
            given { auth().basic(user, user) } `when` { get("/api/books") } then { statusCode(expectedStatus) }
        }

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

    private fun <T : Any> asUser(body: () -> T): T = executeAsUserWithRole(role = Authorizations.USER_ROLE, body = body)
    private fun <T : Any> asCurator(body: () -> T): T = executeAsUserWithRole(role = Authorizations.CURATOR_ROLE, body = body)

}