package library.service.api.books

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.willReturn
import library.service.api.correlation.CorrelationIdHolder
import library.service.business.books.BookDataStore
import library.service.business.books.BookIdGenerator
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.events.BookEvent
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.events.EventDispatcher
import library.service.security.UserContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.Books
import utils.classification.IntegrationTest
import utils.clockWithFixedTime
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@IntegrationTest
@ExtendWith(SpringExtension::class)
@WebMvcTest(BooksController::class, secure = false)
@ActiveProfiles("test", "books-controller-test")
internal class BooksControllerIntTest {

    @TestConfiguration
    @Profile("books-controller-test")
    @ComponentScan("library.service.api.books", "library.service.business.books")
    class AdditionalConfiguration {
        @Bean fun clock(): Clock = clockWithFixedTime("2017-08-20T12:34:56.789Z")
    }

    val correlationId = UUID.randomUUID().toString()

    @SpyBean lateinit var correlationIdHolder: CorrelationIdHolder
    @SpyBean lateinit var userContext: UserContext
    @MockBean lateinit var bookDataStore: BookDataStore
    @MockBean lateinit var bookIdGenerator: BookIdGenerator
    @MockBean lateinit var bookEventDispatcher: EventDispatcher<BookEvent>

    @Autowired lateinit var mockMvc: MockMvc

    @BeforeEach fun initMocks() {
        given { bookDataStore.createOrUpdate(any()) }.willAnswer { it.arguments[0] as BookRecord }
    }

    @Nested inner class `get all books` {

        @Test fun `returns near empty response if there are no books`() {
            val request = get("/api/books")
            val expectedResponse = """
                {
                  "_links": {
                      "self": { "href": "http://localhost/api/books" }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns response containing all books`() {
            val availableBook = availableBook(
                    id = "883a2931-325b-4482-8972-8cb6f7d33816",
                    book = Books.CLEAN_CODE
            )
            val borrowedBook = borrowedBook(
                    id = "53397dc0-932d-4198-801a-3e00b2742ba7",
                    book = Books.CLEAN_CODER,
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findAll() }.willReturn(listOf(availableBook, borrowedBook))

            val request = get("/api/books")
            val expectedResponse = """
                {
                  "_embedded": {
                    "books": [
                      {
                        "isbn": "${Books.CLEAN_CODE.isbn}",
                        "title": "${Books.CLEAN_CODE.title}",
                        "authors": ["${Books.CLEAN_CODE.authors[0]}"],
                        "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                        "_links": {
                          "self": {
                            "href": "http://localhost/api/books/883a2931-325b-4482-8972-8cb6f7d33816"
                          },
                          "delete": {
                            "href": "http://localhost/api/books/883a2931-325b-4482-8972-8cb6f7d33816"
                          },
                          "borrow": {
                            "href": "http://localhost/api/books/883a2931-325b-4482-8972-8cb6f7d33816/borrow"
                          }
                        }
                      },
                      {
                        "isbn": "${Books.CLEAN_CODER.isbn}",
                        "title": "${Books.CLEAN_CODER.title}",
                        "authors": ["${Books.CLEAN_CODER.authors[0]}"],
                        "numberOfPages": ${Books.CLEAN_CODER.numberOfPages},
                        "borrowed": {
                          "by": "Uncle Bob",
                          "on": "2017-08-20T12:34:56.789Z"
                        },
                        "_links": {
                          "self": {
                            "href": "http://localhost/api/books/53397dc0-932d-4198-801a-3e00b2742ba7"
                          },
                          "delete": {
                            "href": "http://localhost/api/books/53397dc0-932d-4198-801a-3e00b2742ba7"
                          },
                          "return": {
                            "href": "http://localhost/api/books/53397dc0-932d-4198-801a-3e00b2742ba7/return"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `post book` {

        @Test fun `returns response containing created book`() {
            val bookId = BookId.generate()
            given { bookIdGenerator.generate() } willReturn { bookId }

            val requestBody = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship"
                }
            """
            val request = post("/api/books")
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(requestBody)
            val expectedResponse = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
                  "authors": [],
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books/$bookId"
                    },
                    "delete": {
                      "href": "http://localhost/api/books/$bookId"
                    },
                    "borrow": {
                      "href": "http://localhost/api/books/$bookId/borrow"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ISBN is not valid`() {
            val requestBody = """
                {
                  "isbn": "abcdefghij",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship"
                }
            """
            val request = post("/api/books")
                    .header("X-Correlation-ID", correlationId)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(requestBody)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "This is not a valid ISBN-13 number: 978abcdefghij"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request content invalid`() {
            val request = post("/api/books")
                    .header("X-Correlation-ID", correlationId)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(" { } ")
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's body is invalid. See details...",
                  "details": [
                    "The field 'isbn' must not be blank.",
                    "The field 'title' must not be blank."
                  ]
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request malformed`() {
            val request = post("/api/books")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's body could not be read. It is either empty or malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `get book by ID` {

        @Test fun `returns response containing available book`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val availableBook = availableBook(
                    id = idValue,
                    book = Books.CLEAN_CODE
            )
            given { bookDataStore.findById(id) }.willReturn(availableBook)

            val request = get("/api/books/$idValue")
            val expectedResponse = """
                {
                  "isbn": "${Books.CLEAN_CODE.isbn}",
                  "title": "${Books.CLEAN_CODE.title}",
                  "authors": ["${Books.CLEAN_CODE.authors[0]}"],
                  "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "delete": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "borrow": {
                      "href": "http://localhost/api/books/$idValue/borrow"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns response containing borrowed book`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val borrowedBook = borrowedBook(
                    id = idValue,
                    book = Books.CLEAN_CODER,
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(borrowedBook)

            val request = get("/api/books/$idValue")
            val expectedResponse = """
                {
                  "isbn": "${Books.CLEAN_CODER.isbn}",
                  "title": "${Books.CLEAN_CODER.title}",
                  "authors": ["${Books.CLEAN_CODER.authors[0]}"],
                  "numberOfPages": ${Books.CLEAN_CODER.numberOfPages},
                  "borrowed": {
                    "by": "Uncle Bob",
                    "on": "2017-08-20T12:34:56.789Z"
                  },
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "delete": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "return": {
                      "href": "http://localhost/api/books/$idValue/return"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            given { bookDataStore.findById(id) }.willReturn(null)

            val request = get("/api/books/$idValue")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = get("/api/books/$idValue")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `delete book by ID` {

        @Test fun `returns empty response if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = availableBook(
                    id = idValue,
                    book = Books.CLEAN_CODE
            )
            given { bookDataStore.findById(id) }.willReturn(book)

            mockMvc.perform(delete("/api/books/$idValue"))
                    .andExpect(status().isNoContent)
        }

        @Test fun `returns error response (404) if book was not found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            given { bookDataStore.findById(id) }.willReturn(null)

            val request = delete("/api/books/$idValue")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = delete("/api/books/$idValue")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `borrow book by ID` {

        @Test fun `returns response containing updated book if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = availableBook(
                    id = idValue,
                    book = Books.CLEAN_CODE
            )
            given { bookDataStore.findById(id) }.willReturn(book)

            val request = post("/api/books/$idValue/borrow")
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "isbn": "${Books.CLEAN_CODE.isbn}",
                  "title": "${Books.CLEAN_CODE.title}",
                  "authors": ["${Books.CLEAN_CODE.authors[0]}"],
                  "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                  "borrowed": {
                    "by": "Uncle Bob",
                    "on": "2017-08-20T12:34:56.789Z"
                  },
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "delete": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "return": {
                      "href": "http://localhost/api/books/$idValue/return"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (409) if book already borrowed`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val borrowedBook = borrowedBook(
                    id = idValue,
                    book = Books.CLEAN_CODE,
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(borrowedBook)

            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", correlationId)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "status": 409,
                  "error": "Conflict",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue is already borrowed!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isConflict)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", correlationId)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request content invalid`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", correlationId)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(" { } ")
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's body is invalid. See details...",
                  "details": [ "The field 'borrower' must not be blank." ]
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request malformed`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's body could not be read. It is either empty or malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `return book by ID` {

        @Test fun `returns response containing updated book if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = borrowedBook(
                    id = idValue,
                    book = Books.CLEAN_CODE,
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(book)

            val request = post("/api/books/$idValue/return")
            val expectedResponse = """
                {
                  "isbn": "${Books.CLEAN_CODE.isbn}",
                  "title": "${Books.CLEAN_CODE.title}",
                  "authors": ["${Books.CLEAN_CODE.authors[0]}"],
                  "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                  "_links": {
                    "self": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "delete": {
                      "href": "http://localhost/api/books/$idValue"
                    },
                    "borrow": {
                      "href": "http://localhost/api/books/$idValue/borrow"
                    }
                  }
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(HAL_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (409) if book already returned`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val availableBook = availableBook(
                    id = idValue,
                    book = Books.CLEAN_CODE
            )
            given { bookDataStore.findById(id) }.willReturn(availableBook)

            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 409,
                  "error": "Conflict",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue was already returned!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isConflict)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", correlationId)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$correlationId",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    private fun borrowedBook(id: String, book: Book, borrowedBy: String, borrowedOn: String): BookRecord {
        val bookEntity = availableBook(id, book)
        bookEntity.borrow(Borrower(borrowedBy), OffsetDateTime.parse(borrowedOn))
        return bookEntity
    }

    private fun availableBook(id: String, book: Book): BookRecord {
        return BookRecord(BookId.from(id), book)
    }

}