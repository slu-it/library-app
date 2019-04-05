package library.service.api.books

import io.mockk.every
import io.mockk.mockk
import library.service.business.books.BookCollection
import library.service.business.books.BookDataStore
import library.service.business.books.BookIdGenerator
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Author
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.correlation.CorrelationIdHolder
import library.service.security.UserContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.Books
import utils.MutableClock
import utils.ResetMocksAfterEachTest
import utils.classification.IntegrationTest
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@IntegrationTest
@ResetMocksAfterEachTest
@WebMvcTest(BooksController::class, secure = false)
internal class BooksControllerIntTest(
    @Autowired val bookDataStore: BookDataStore,
    @Autowired val bookIdGenerator: BookIdGenerator,
    @Autowired val mockMvc: MockMvc,
    @Autowired val clock: MutableClock
) {

    @TestConfiguration
    class AdditionalBeans {
        @Bean fun correlationIdHolder() = CorrelationIdHolder()
        @Bean fun bookResourceAssembler() = BookResourceAssembler(UserContext())
        @Bean fun bookCollection(clock: Clock) = BookCollection(
            clock = clock,
            dataStore = bookDataStore(),
            idGenerator = bookIdGenerator(),
            eventDispatcher = mockk(relaxed = true)
        )

        @Bean fun bookDataStore(): BookDataStore = mockk()
        @Bean fun bookIdGenerator(): BookIdGenerator = mockk()
    }

    val correlationId = UUID.randomUUID().toString()

    @BeforeEach fun setTime() {
        clock.setFixedTime("2017-08-20T12:34:56.789Z")
    }

    @BeforeEach fun initMocks() {
        every { bookDataStore.findById(any()) } returns null
        every { bookDataStore.createOrUpdate(any()) } answers { firstArg() }
    }

    @DisplayName("/api/books")
    @Nested inner class BooksEndpoint {

        @DisplayName("GET")
        @Nested inner class GetMethod {

            @Test fun `when there are no books, the response only contains a self link`() {
                every { bookDataStore.findAll() } returns emptyList()
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

            @Test fun `when there are books, the response contains them with all relevant links`() {
                val availableBook = availableBook(
                    id = BookId.from("883a2931-325b-4482-8972-8cb6f7d33816"),
                    book = Books.CLEAN_CODE
                )
                val borrowedBook = borrowedBook(
                    id = BookId.from("53397dc0-932d-4198-801a-3e00b2742ba7"),
                    book = Books.CLEAN_CODER,
                    borrowedBy = "Uncle Bob",
                    borrowedOn = "2017-08-20T12:34:56.789Z"
                )
                every { bookDataStore.findAll() } returns listOf(availableBook, borrowedBook)

                val request = get("/api/books")
                val expectedResponse = """
                    {
                      "_embedded": {
                        "books": [
                          {
                            "isbn": "${Books.CLEAN_CODE.isbn}",
                            "title": "${Books.CLEAN_CODE.title}",
                            "authors": ${Books.CLEAN_CODE.authors.toJson()},
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
                            "authors": ${Books.CLEAN_CODER.authors.toJson()},
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

        @DisplayName("POST")
        @Nested inner class PostMethod {

            @Test fun `creates a book and responds with its resource representation`() {
                val bookId = BookId.generate()
                every { bookIdGenerator.generate() } returns bookId

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

            @Test fun `400 BAD REQUEST for invalid ISBN`() {
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
                      "message": "The request's body is invalid. See details...",
                      "details": ["The field 'isbn' must match \"(\\d{3}-?)?\\d{10}\"."]
                    }
                """
                mockMvc.perform(request)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().json(expectedResponse, true))
            }

            @Test fun `400 BAD REQUEST for missing required properties`() {
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

            @Test fun `400 BAD REQUEST for malformed request`() {
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

        @DisplayName("/api/books/{id}")
        @Nested inner class BookByIdEndpoint {

            val id = BookId.generate()
            val book = Books.CLEAN_CODE
            val availableBookRecord = availableBook(id, book)
            val borrowedBookRecord = borrowedBook(id, book, "Uncle Bob", "2017-08-20T12:34:56.789Z")

            @DisplayName("GET")
            @Nested inner class GetMethod {

                @Test fun `responds with book's resource representation for existing available book`() {
                    every { bookDataStore.findById(id) } returns availableBookRecord

                    val request = get("/api/books/$id")
                    val expectedResponse = """
                        {
                          "isbn": "${Books.CLEAN_CODE.isbn}",
                          "title": "${Books.CLEAN_CODE.title}",
                          "authors": ${Books.CLEAN_CODE.authors.toJson()},
                          "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/books/$id"
                            },
                            "delete": {
                              "href": "http://localhost/api/books/$id"
                            },
                            "borrow": {
                              "href": "http://localhost/api/books/$id/borrow"
                            }
                          }
                        }
                    """
                    mockMvc.perform(request)
                        .andExpect(status().isOk)
                        .andExpect(content().contentType(HAL_JSON_UTF8))
                        .andExpect(content().json(expectedResponse, true))
                }

                @Test fun `responds with book's resource representation for existing borrowed book`() {
                    every { bookDataStore.findById(id) } returns borrowedBookRecord

                    val request = get("/api/books/$id")
                    val expectedResponse = """
                        {
                          "isbn": "${Books.CLEAN_CODE.isbn}",
                          "title": "${Books.CLEAN_CODE.title}",
                          "authors": ${Books.CLEAN_CODE.authors.toJson()},
                          "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                          "borrowed": {
                            "by": "Uncle Bob",
                            "on": "2017-08-20T12:34:56.789Z"
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/books/$id"
                            },
                            "delete": {
                              "href": "http://localhost/api/books/$id"
                            },
                            "return": {
                              "href": "http://localhost/api/books/$id/return"
                            }
                          }
                        }
                    """
                    mockMvc.perform(request)
                        .andExpect(status().isOk)
                        .andExpect(content().contentType(HAL_JSON_UTF8))
                        .andExpect(content().json(expectedResponse, true))
                }

                @Test fun `404 NOT FOUND for non-existing book`() {
                    val request = get("/api/books/$id")
                        .header("X-Correlation-ID", correlationId)
                    val expectedResponse = """
                        {
                          "status": 404,
                          "error": "Not Found",
                          "timestamp": "2017-08-20T12:34:56.789Z",
                          "correlationId": "$correlationId",
                          "message": "The book with ID: $id does not exist!"
                        }
                    """
                    mockMvc.perform(request)
                        .andExpect(status().isNotFound)
                        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                        .andExpect(content().json(expectedResponse, true))
                }

                @Test fun `400 BAD REQUEST for malformed ID`() {
                    val request = get("/api/books/malformed-id")
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

            @DisplayName("DELETE")
            @Nested inner class DeleteMethod {

                @Test fun `existing book is deleted and response is empty 204 NO CONTENT`() {
                    every { bookDataStore.findById(id) } returns availableBookRecord
                    every { bookDataStore.delete(availableBookRecord) } returns Unit

                    mockMvc.perform(delete("/api/books/$id"))
                        .andExpect(status().isNoContent)
                }

                @Test fun `404 NOT FOUND for non-existing book`() {
                    val request = delete("/api/books/$id")
                        .header("X-Correlation-ID", correlationId)
                    val expectedResponse = """
                        {
                          "status": 404,
                          "error": "Not Found",
                          "timestamp": "2017-08-20T12:34:56.789Z",
                          "correlationId": "$correlationId",
                          "message": "The book with ID: $id does not exist!"
                        }
                    """
                    mockMvc.perform(request)
                        .andExpect(status().isNotFound)
                        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                        .andExpect(content().json(expectedResponse, true))
                }

                @Test fun `400 BAD REQUEST for malformed ID`() {
                    val request = delete("/api/books/malformed-id")
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

            @DisplayName("/api/books/{id}/authors")
            @Nested inner class BookByIdAuthorsEndpoint {

                @DisplayName("PUT")
                @Nested inner class PutMethod {

                    @Test fun `replaces authors of book and responds with its resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = put("/api/books/$id/authors")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "authors": ["Foo", "Bar"] } """)
                        val expectedResponse = """
                            {
                              "isbn": "${book.isbn}",
                              "title": "${book.title}",
                              "authors": ["Foo", "Bar"],
                              "numberOfPages": ${book.numberOfPages},
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = put("/api/books/$id/authors")
                            .header("X-Correlation-ID", correlationId)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "authors": ["Foo", "Bar"] } """)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for missing required properties`() {
                        val request = put("/api/books/$id/authors")
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
                              "details": [ "The field 'authors' must not be empty." ]
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isBadRequest)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                }

                @DisplayName("DELETE")
                @Nested inner class DeleteMethod {

                    @Test fun `removes authors from book and responds with its resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = delete("/api/books/$id/authors")
                        val expectedResponse = """
                            {
                              "isbn": "${book.isbn}",
                              "title": "${book.title}",
                              "authors": [],
                              "numberOfPages": ${book.numberOfPages},
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = delete("/api/books/$id/authors")
                            .header("X-Correlation-ID", correlationId)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                }

            }

            @DisplayName("/api/books/{id}/borrow")
            @Nested inner class BookByIdBorrowEndpoint {

                @DisplayName("POST")
                @Nested inner class PostMethod {

                    @Test fun `borrows book and responds with its updated resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = post("/api/books/$id/borrow")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "borrower": "Uncle Bob" } """)
                        val expectedResponse = """
                            {
                              "isbn": "${Books.CLEAN_CODE.isbn}",
                              "title": "${Books.CLEAN_CODE.title}",
                              "authors": ${Books.CLEAN_CODE.authors.toJson()},
                              "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                              "borrowed": {
                                "by": "Uncle Bob",
                                "on": "2017-08-20T12:34:56.789Z"
                              },
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "return": {
                                  "href": "http://localhost/api/books/$id/return"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `409 CONFLICT for already borrowed book`() {
                        every { bookDataStore.findById(id) } returns borrowedBookRecord

                        val request = post("/api/books/$id/borrow")
                            .header("X-Correlation-ID", correlationId)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "borrower": "Uncle Bob" } """)
                        val expectedResponse = """
                            {
                              "status": 409,
                              "error": "Conflict",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id is already borrowed!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isConflict)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = post("/api/books/$id/borrow")
                            .header("X-Correlation-ID", correlationId)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "borrower": "Uncle Bob" } """)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for missing required properties`() {
                        val request = post("/api/books/$id/borrow")
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
                              "details": [ "The field 'borrower' must not be null." ]
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isBadRequest)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for malformed request`() {
                        val request = post("/api/books/$id/borrow")
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

                    @Test fun `400 BAD REQUEST for malformed ID`() {
                        val request = post("/api/books/malformed-id/borrow")
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

            }

            @DisplayName("/api/books/{id}/numberOfPages")
            @Nested inner class BookByIdNumberOfPagesEndpoint {

                @DisplayName("PUT")
                @Nested inner class PutMethod {

                    @Test fun `replaces number of pages of book and responds with its resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = put("/api/books/$id/numberOfPages")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "numberOfPages": 128 } """)
                        val expectedResponse = """
                            {
                              "isbn": "${book.isbn}",
                              "title": "${book.title}",
                              "authors": ${book.authors.toJson()},
                              "numberOfPages": 128,
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = put("/api/books/$id/numberOfPages")
                            .header("X-Correlation-ID", correlationId)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "numberOfPages": 128 } """)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for missing required properties`() {
                        val idValue = BookId.generate().toString()
                        val request = put("/api/books/$idValue/numberOfPages")
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
                              "details": [ "The field 'numberOfPages' must not be null." ]
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isBadRequest)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                }

                @DisplayName("DELETE")
                @Nested inner class DeleteMethod {

                    @Test fun `removes number of pages from book and responds with its resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = delete("/api/books/$id/numberOfPages")
                        val expectedResponse = """
                            {
                              "isbn": "${book.isbn}",
                              "title": "${book.title}",
                              "authors": ${book.authors.toJson()},
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = delete("/api/books/$id/numberOfPages")
                            .header("X-Correlation-ID", correlationId)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                }

            }

            @DisplayName("/api/books/{id}/return")
            @Nested inner class BookByIdReturnEndpoint {

                @DisplayName("POST")
                @Nested inner class PostMethod {

                    @Test fun `returns book and responds with its updated resource representation`() {
                        every { bookDataStore.findById(id) } returns borrowedBookRecord

                        val request = post("/api/books/$id/return")
                        val expectedResponse = """
                            {
                              "isbn": "${Books.CLEAN_CODE.isbn}",
                              "title": "${Books.CLEAN_CODE.title}",
                              "authors": ${Books.CLEAN_CODE.authors.toJson()},
                              "numberOfPages": ${Books.CLEAN_CODE.numberOfPages},
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `409 CONFLICT for already returned book`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = post("/api/books/$id/return")
                            .header("X-Correlation-ID", correlationId)
                        val expectedResponse = """
                            {
                              "status": 409,
                              "error": "Conflict",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id was already returned!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isConflict)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = post("/api/books/$id/return")
                            .header("X-Correlation-ID", correlationId)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for malformed ID`() {
                        val request = post("/api/books/malformed-id/return")
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

            }

            @DisplayName("/api/books/{id}/title")
            @Nested inner class BookByIdTitleEndpoint {

                @DisplayName("PUT")
                @Nested inner class PutMethod {

                    @Test fun `replaces title of book and responds with its resource representation`() {
                        every { bookDataStore.findById(id) } returns availableBookRecord

                        val request = put("/api/books/$id/title")
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "title": "New Title" } """)
                        val expectedResponse = """
                            {
                              "isbn": "${book.isbn}",
                              "title": "New Title",
                              "authors": ${book.authors.toJson()},
                              "numberOfPages": ${book.numberOfPages},
                              "_links": {
                                "self": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "delete": {
                                  "href": "http://localhost/api/books/$id"
                                },
                                "borrow": {
                                  "href": "http://localhost/api/books/$id/borrow"
                                }
                              }
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isOk)
                            .andExpect(content().contentType(HAL_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `404 NOT FOUND for non-existing book`() {
                        val request = put("/api/books/$id/title")
                            .header("X-Correlation-ID", correlationId)
                            .contentType(APPLICATION_JSON_UTF8)
                            .content(""" { "title": "New Title" } """)
                        val expectedResponse = """
                            {
                              "status": 404,
                              "error": "Not Found",
                              "timestamp": "2017-08-20T12:34:56.789Z",
                              "correlationId": "$correlationId",
                              "message": "The book with ID: $id does not exist!"
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isNotFound)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                    @Test fun `400 BAD REQUEST for missing required properties`() {
                        val idValue = BookId.generate().toString()
                        val request = put("/api/books/$idValue/title")
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
                              "details": [ "The field 'title' must not be blank." ]
                            }
                        """
                        mockMvc.perform(request)
                            .andExpect(status().isBadRequest)
                            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                            .andExpect(content().json(expectedResponse, true))
                    }

                }

            }

        }

    }

    private fun availableBook(id: BookId, book: Book) = BookRecord(id, book)
    private fun borrowedBook(id: BookId, book: Book, borrowedBy: String, borrowedOn: String) = availableBook(id, book)
        .borrow(Borrower(borrowedBy), OffsetDateTime.parse(borrowedOn))

    private fun List<Author>.toJson() = joinToString(separator = "\", \"", prefix = "[\"", postfix = "\"]")

}