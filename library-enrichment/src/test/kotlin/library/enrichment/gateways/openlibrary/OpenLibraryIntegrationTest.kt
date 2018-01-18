package library.enrichment.gateways.openlibrary

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import library.enrichment.gateways.openlibrary.OpenLibraryIntegrationTest.CustomConfiguration
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
@ContextConfiguration(classes = [CustomConfiguration::class])
internal class OpenLibraryIntegrationTest {

    @ComponentScan(basePackageClasses = [OpenLibraryAccessor::class])
    class CustomConfiguration

    @Autowired lateinit var settings: OpenLibrarySettings
    @Autowired lateinit var accessor: OpenLibraryAccessor

    val testIsbn = "1234567890"

    @BeforeEach fun configureWireMockAsTarget(wireMock: WireMockServer) {
        settings.url = "http://localhost:${wireMock.port()}"
    }

    @Test fun `representative response is processed correctly`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse("0261102354", 200) {
            fromFile("200_isbn_0261102354.json")
        }
        val bookData = getBookData("0261102354")
        with(bookData) {
            assertThat(authors).containsExactly("J. R. R. Tolkien")
            assertThat(numberOfPages).isEqualTo(576)
        }
    }

    @Test fun `all BookData properties are read correctly if book was found`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse(testIsbn) {
            """
            {
              "ISBN:$testIsbn": {
                "title": "The Title",
                "number_of_pages": 42,
                "authors": [ { "name": "The Author" } ]
              }
            }
            """
        }
        val bookData = getBookData(testIsbn)
        with(bookData) {
            assertThat(authors).containsExactly("The Author")
            assertThat(numberOfPages).isEqualTo(42)
        }
    }

    @Test fun `books can have multiple authors`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse(testIsbn) {
            """
            {
              "ISBN:$testIsbn": {
                "title": "The Title",
                "number_of_pages": 42,
                "authors": [ { "name": "The Author" }, { "name": "Another Author" } ]
              }
            }
            """
        }
        val bookData = getBookData(testIsbn)
        assertThat(bookData.authors).containsExactly("The Author", "Another Author")
    }

    @Test fun `empty search result will result in null return value`(wireMock: WireMockServer) {
        wireMock.stubBookSearchResponse(testIsbn) { "{}" }
        val bookData = getOptionalBookData(testIsbn)
        assertThat(bookData).isNull()
    }

    @Nested inner class `processing can handle incomplete data` {

        @Test fun `missing number of pages property`(wireMock: WireMockServer) {
            wireMock.stubBookSearchResponse(testIsbn) {
                """
                {
                  "ISBN:$testIsbn": {
                    "title": "The Title",
                    "authors": [ { "name": "The Author" } ]
                  }
                }
                """
            }
            val bookData = getBookData(testIsbn)
            assertThat(bookData.numberOfPages).isNull()
        }

        @Test fun `missing authors property`(wireMock: WireMockServer) {
            wireMock.stubBookSearchResponse(testIsbn) {
                """
                {
                  "ISBN:$testIsbn": {
                    "title": "The Title",
                    "number_of_pages": 42
                  }
                }
                """
            }
            val bookData = getBookData(testIsbn)
            assertThat(bookData.authors).isEmpty()
        }

    }

    @Nested inner class `error cases` {

        @RecordLoggers(OpenLibraryAccessor::class)
        @ValueSource(ints = intArrayOf(500, 502, 503, 504))
        @ParameterizedTest fun `internal server errors are logged as warnings`(status: Int, wireMock: WireMockServer, log: LogRecord) {
            wireMock.givenThat(get(urlEqualTo(bookSearchUrl(testIsbn))).willReturn(aResponse().withStatus(status)))
            getOptionalBookData(testIsbn)

            assertThat(log.warningMessages())
                    .containsOnly("Could not retrieve book data from openlibrary.org because of an error on their end:")
        }

        @RecordLoggers(OpenLibraryAccessor::class)
        @ValueSource(ints = intArrayOf(400, 401, 403, 404, 405, 406, 409, 410))
        @ParameterizedTest fun `client errors are logged as errors`(status: Int, wireMock: WireMockServer, log: LogRecord) {
            wireMock.givenThat(get(urlEqualTo(bookSearchUrl(testIsbn))).willReturn(aResponse().withStatus(status)))
            getOptionalBookData(testIsbn)

            assertThat(log.errorMessages())
                    .containsOnly("Could not retrieve book data from openlibrary.org because of an error on our end:")
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

    fun bookSearchUrl(isbn: String) = "/api/books?bibkeys=ISBN%3A$isbn&format=json&jscmd=data"
    fun fromFile(fileName: String) = readFile("openlibrary/responses/$fileName")

}