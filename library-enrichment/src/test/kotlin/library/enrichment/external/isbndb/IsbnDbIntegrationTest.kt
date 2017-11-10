package library.enrichment.external.isbndb

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import library.enrichment.external.isbndb.IsbnDbIntegrationTest.CustomConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.IntegrationTest
import utils.errorMessages
import utils.extensions.EnableSpringExtension
import utils.extensions.EnableWireMockExtension
import utils.readFile
import utils.warningMessages

@SpringBootTest
@IntegrationTest
@EnableSpringExtension
@EnableWireMockExtension
@ContextConfiguration(classes = arrayOf(CustomConfiguration::class))
internal class IsbnDbIntegrationTest {

    @ComponentScan("library.enrichment.external.isbndb")
    class CustomConfiguration

    @Autowired lateinit var settings: IsbnDbSettings
    @Autowired lateinit var accessor: IsbnDbAccessor

    val testIsbn = "1234567890"

    @BeforeEach fun configureWireMockAsTarget(wireMock: WireMockServer) {
        settings.url = "http://localhost:${wireMock.port()}"
        settings.apiKey = "ABCDEF"
    }

    @Test fun `representative found response is processed correctly`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse("0132350882", 200) {
            fromFile("200_found.json")
        }
        val bookData = getBookData("0132350882")
        with(bookData) {
            assertThat(title).isEqualTo("Clean code")
            assertThat(authors).containsOnly("Martin, Robert W. T.", "Michael Feathers")
            assertThat(numberOfPages).isNull()
        }
    }

    @Test fun `representative not found response is processed correctly`(wireMock: WireMockServer) {
        // service does not implement HTTP correctly - not found is a 200 OK with an error in the body
        wireMock.stubBookSearchResponse("013235088X", 200) {
            fromFile("200_not_found.json")
        }
        val bookData = getOptionalBookData("013235088X")
        assertThat(bookData).isNull()
    }

    @Test fun `all BookData properties are read correctly if book was found`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse(testIsbn) {
            """
            {
              "data": [
                {
                  "title": "The Title",
                  "author_data": [ { "name": "The Author" } ]
                }
              ]
            }
            """
        }
        val bookData = getBookData(testIsbn)
        with(bookData) {
            assertThat(title).isEqualTo("The Title")
            assertThat(authors).containsExactly("The Author")
            assertThat(numberOfPages).isNull()
        }
    }

    @Test fun `books can have multiple authors`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse(testIsbn) {
            """
            {
              "data": [
                {
                  "title": "The Title",
                  "author_data": [ { "name": "The Author" }, { "name": "Another Author" } ]
                }
              ]
            }
            """
        }
        val bookData = getBookData(testIsbn)
        assertThat(bookData.authors).containsExactly("The Author", "Another Author")
    }

    @Nested inner class `processing can handle incomplete data` {

        @Test fun `missing title property`(wireMock: WireMockServer) {
            wireMock.stubBookSearchResponse(testIsbn) {
                """
                {
                  "data": [
                    {
                      "author_data": [ { "name": "The Author" } ]
                    }
                  ]
                }
                """
            }
            val bookData = getBookData(testIsbn)
            assertThat(bookData.title).isNull()
        }

        @Test fun `missing authors property`(wireMock: WireMockServer) {
            wireMock.stubBookSearchResponse(testIsbn) {
                """
                {
                  "data": [
                    {
                      "title": "The Title"
                    }
                  ]
                }
                """
            }
            val bookData = getBookData(testIsbn)
            assertThat(bookData.authors).isEmpty()
        }

    }

    @Nested inner class `error cases` {

        @RecordLoggers(IsbnDbAccessor::class)
        @ValueSource(ints = intArrayOf(500, 502, 503, 504))
        @ParameterizedTest fun `internal server errors are logged as warnings`(status: Int, wireMock: WireMockServer, log: LogRecord) {
            wireMock.givenThat(get(urlEqualTo(bookSearchUrl(testIsbn))).willReturn(aResponse().withStatus(status)))
            getOptionalBookData(testIsbn)

            assertThat(log.warningMessages())
                    .containsOnly("Could not retrieve book data from isbndb.com because of an error on their end:")
        }

        @RecordLoggers(IsbnDbAccessor::class)
        @ValueSource(ints = intArrayOf(400, 401, 403, 404, 405, 406, 409, 410))
        @ParameterizedTest fun `client errors are logged as errors`(status: Int, wireMock: WireMockServer, log: LogRecord) {
            wireMock.givenThat(get(urlEqualTo(bookSearchUrl(testIsbn))).willReturn(aResponse().withStatus(status)))
            getOptionalBookData(testIsbn)

            assertThat(log.errorMessages())
                    .containsOnly("Could not retrieve book data from isbndb.com because of an error on our end:")
        }

    }

    fun WireMockServer.stubBookSearchResponse(searchIsbn: String, status: Int = 200, responseBodySupplier: () -> String) {
        givenThat(get(urlEqualTo(bookSearchUrl(searchIsbn)))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodySupplier())))
    }

    fun getBookData(isbn: String) = getOptionalBookData(isbn)!!
    fun getOptionalBookData(isbn: String) = accessor.getBookData(isbn)

    fun bookSearchUrl(isbn: String) = "/api/v2/json/ABCDEF/book/$isbn"
    fun fromFile(fileName: String) = readFile("isbndb/responses/$fileName")

}