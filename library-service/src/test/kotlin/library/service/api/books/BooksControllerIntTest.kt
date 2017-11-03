package library.service.api.books

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import library.service.api.ErrorHandlers
import library.service.business.books.BookDataStore
import library.service.business.books.BookEventDispatcher
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.common.correlation.CorrelationIdHolder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.classification.IntegrationTest
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

@WebMvcTest
@IntegrationTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = arrayOf(BooksControllerIntTest.TestConfiguration::class))
internal class BooksControllerIntTest {

    @ComponentScan("library.service.api.books", "library.service.business.books", "library.service.common")
    class TestConfiguration {
        @Bean fun clock(): Clock = Clock.fixed(OffsetDateTime.parse("2017-08-20T12:34:56.789Z").toInstant(), ZoneId.of("UTC"))
        @Bean fun errorHandlers(clock: Clock, correlationIdHolder: CorrelationIdHolder) = ErrorHandlers(clock, correlationIdHolder)
    }

    val APPLICATION_JSON = "application/json;charset=UTF-8"
    val APPLICATION_HAL_JSON = "application/hal+json;charset=UTF-8"
    val CORRELATION_ID = UUID.randomUUID().toString()

    @MockBean lateinit var bookDataStore: BookDataStore
    @MockBean lateinit var bookeEventDispatcher: BookEventDispatcher
    @Autowired lateinit var mockMvc: MockMvc

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
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns response containing all books`() {
            val availableBook = availableBook(
                    id = "883a2931-325b-4482-8972-8cb6f7d33816",
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship"
            )
            val borrowedBook = borrowedBook(
                    id = "53397dc0-932d-4198-801a-3e00b2742ba7",
                    isbn = "9780137081073",
                    title = "The Clean Coder: A Code of Conduct for Professional Programmers",
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
                        "isbn": "9780132350884",
                        "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
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
                        "isbn": "9780137081073",
                        "title": "The Clean Coder: A Code of Conduct for Professional Programmers",
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
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `post book` {

        @Test fun `returns response containing created book`() {
            val id = BookId.generate()
            val idValue = id.toString()
            given { bookDataStore.create(any()) }.willAnswer {
                val book = it.arguments[0] as Book
                BookRecord(id, book)
            }

            val requestBody = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship"
                }
            """
            val request = post("/api/books")
                    .contentType(APPLICATION_JSON)
                    .content(requestBody)
            val expectedResponse = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
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
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
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
                    .header("X-Correlation-ID", CORRELATION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(requestBody)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "This is not a valid ISBN-13 number: 978abcdefghij"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request content invalid`() {
            val request = post("/api/books")
                    .header("X-Correlation-ID", CORRELATION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(" { } ")
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's body is invalid. See details...",
                  "details": [
                    "The field 'isbn' must not be blank.",
                    "The field 'title' must not be blank."
                  ]
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request malformed`() {
            val request = post("/api/books")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's body could not be read. It is either empty or malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `get book by ID` {

        @Test fun `returns response containing available book`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val availableBook = availableBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship"
            )
            given { bookDataStore.findById(id) }.willReturn(availableBook)

            val request = get("/api/books/$idValue")
            val expectedResponse = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
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
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns response containing borrowed book`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val borrowedBook = borrowedBook(
                    id = idValue,
                    isbn = "9780137081073",
                    title = "The Clean Coder: A Code of Conduct for Professional Programmers",
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(borrowedBook)

            val request = get("/api/books/$idValue")
            val expectedResponse = """
                {
                  "isbn": "9780137081073",
                  "title": "The Clean Coder: A Code of Conduct for Professional Programmers",
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
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            given { bookDataStore.findById(id) }.willReturn(null)

            val request = get("/api/books/$idValue")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = get("/api/books/$idValue")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `delete book by ID` {

        @Test fun `returns empty response if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = availableBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship"
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
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = delete("/api/books/$idValue")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `borrow book by ID` {

        @Test fun `returns response containing updated book if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = availableBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship"
            )
            given { bookDataStore.findById(id) }.willReturn(book)
            given { bookDataStore.update(book) }.willReturn(book)

            val request = post("/api/books/$idValue/borrow")
                    .contentType(APPLICATION_JSON)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
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
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (409) if book already borrowed`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val borrowedBook = borrowedBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship",
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(borrowedBook)

            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", CORRELATION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "status": 409,
                  "error": "Conflict",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue is already borrowed!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isConflict)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", CORRELATION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(""" { "borrower": "Uncle Bob" } """)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request content invalid`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", CORRELATION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(" { } ")
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's body is invalid. See details...",
                  "details": [ "The field 'borrower' must not be blank." ]
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if request malformed`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's body could not be read. It is either empty or malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = post("/api/books/$idValue/borrow")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    @Nested inner class `return book by ID` {

        @Test fun `returns response containing updated book if book was found`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val book = borrowedBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship",
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
            )
            given { bookDataStore.findById(id) }.willReturn(book)
            given { bookDataStore.update(book) }.willReturn(book)

            val request = post("/api/books/$idValue/return")
            val expectedResponse = """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code: A Handbook of Agile Software Craftsmanship",
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
                    .andExpect(content().contentType(APPLICATION_HAL_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (409) if book already returned`() {
            val id = BookId.generate()
            val idValue = id.toString()
            val availableBook = availableBook(
                    id = idValue,
                    isbn = "9780132350884",
                    title = "Clean Code: A Handbook of Agile Software Craftsmanship"
            )
            given { bookDataStore.findById(id) }.willReturn(availableBook)

            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 409,
                  "error": "Conflict",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue was already returned!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isConflict)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (404) if book was not found`() {
            val idValue = BookId.generate().toString()
            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The book with ID: $idValue does not exist!"
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isNotFound)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

        @Test fun `returns error response (400) if ID parameter malformed`() {
            val idValue = "malformed-id"
            val request = post("/api/books/$idValue/return")
                    .header("X-Correlation-ID", CORRELATION_ID)
            val expectedResponse = """
                {
                  "status": 400,
                  "error": "Bad Request",
                  "timestamp": "2017-08-20T12:34:56.789Z",
                  "correlationId": "$CORRELATION_ID",
                  "message": "The request's 'id' parameter is malformed."
                }
            """
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(content().json(expectedResponse, true))
        }

    }

    private fun borrowedBook(id: String, isbn: String, title: String, borrowedBy: String, borrowedOn: String): BookRecord {
        val bookEntity = availableBook(id, isbn, title)
        bookEntity.borrow(Borrower(borrowedBy), OffsetDateTime.parse(borrowedOn))
        return bookEntity
    }

    private fun availableBook(id: String, isbn: String, title: String): BookRecord {
        return BookRecord(BookId.from(id), Book(Isbn13(isbn), Title(title)))
    }

}