package library.enrichment.gateways.library

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import feign.FeignException
import library.enrichment.correlation.CorrelationId
import library.enrichment.correlation.CorrelationIdRequestInterceptor
import library.enrichment.gateways.library.LibraryIntegrationTest.CustomConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
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
import utils.extensions.EnableSpringExtension
import utils.extensions.EnableWireMockExtension
import java.util.*

@SpringBootTest
@IntegrationTest
@EnableSpringExtension
@EnableWireMockExtension
@ContextConfiguration(classes = [CustomConfiguration::class])
internal class LibraryIntegrationTest {

    @ComponentScan(basePackageClasses = [LibraryAccessor::class, CorrelationIdRequestInterceptor::class])
    class CustomConfiguration

    @Autowired lateinit var settings: LibrarySettings
    @Autowired lateinit var accessor: LibraryAccessor
    @Autowired lateinit var client: LibraryClient
    @Autowired lateinit var correlationIdHolder: CorrelationId

    lateinit var correlationId: String

    @BeforeEach fun setUp(wireMock: WireMockServer) {
        settings.url = "http://localhost:${wireMock.port()}"

        correlationId = UUID.randomUUID().toString()
        correlationIdHolder.setOrGenerate(correlationId)
    }

    @Nested inner class `pinging service` {

        @Test fun `sends correct request to library`(wireMock: WireMockServer): Unit = with(wireMock) {
            givenThat(get(urlEqualTo("/api"))
                    .willReturn(aResponse().withStatus(200)))
            client.ping()
            verify(getRequestedFor(urlEqualTo("/api"))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("Basic Y3VyYXRvcjpjdXJhdG9y"))
                    .withHeader("X-Correlation-ID", equalTo(correlationId)))
        }

        @ValueSource(ints = [400, 401, 403, 404, 500])
        @ParameterizedTest fun `throws exception in case of bad response`(status: Int, wireMock: WireMockServer): Unit = with(wireMock) {
            givenThat(get(urlEqualTo("/api"))
                    .willReturn(aResponse().withStatus(status)))
            assertThrows(FeignException::class.java) {
                client.ping()
            }
        }

    }

    @Nested inner class `updating authors` {

        val bookId = UUID.randomUUID().toString()
        val authors = listOf("J.R.R. Tolkien", "Jim Butcher")

        @Test fun `sends correct request to library`(wireMock: WireMockServer): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/authors"))
                    .willReturn(aResponse().withStatus(200)))
            accessor.updateAuthors(bookId, authors)
            verify(putRequestedFor(urlEqualTo("/api/books/${bookId}/authors"))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("Basic Y3VyYXRvcjpjdXJhdG9y"))
                    .withHeader("X-Correlation-ID", equalTo(correlationId))
                    .withRequestBody(equalToJson(
                            """{ "authors": ["J.R.R. Tolkien", "Jim Butcher"] }"""
                    )))
        }

        @RecordLoggers(LibraryAccessor::class)
        @Test fun `logs successful requests`(wireMock: WireMockServer, log: LogRecord): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/authors"))
                    .willReturn(aResponse().withStatus(200)))
            accessor.updateAuthors(bookId, authors)
            assertThat(log.messages)
                    .contains("successfully updated authors of book [$bookId] to $authors")
        }

        @RecordLoggers(LibraryAccessor::class)
        @ValueSource(ints = [400, 401, 403, 404, 500])
        @ParameterizedTest fun `logs failed requests`(status: Int, wireMock: WireMockServer, log: LogRecord): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/authors"))
                    .willReturn(aResponse().withStatus(status)))
            accessor.updateAuthors(bookId, authors)
            assertThat(log.messages)
                    .contains("failed to update authors of book [$bookId] because of an error:")
        }

    }

    @Nested inner class `updating number of pages` {

        val bookId = UUID.randomUUID().toString()
        val numberOfPages = 256

        @Test fun `sends correct request to library`(wireMock: WireMockServer): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/numberOfPages"))
                    .willReturn(aResponse().withStatus(200)))
            accessor.updateNumberOfPages(bookId, numberOfPages)
            verify(putRequestedFor(urlEqualTo("/api/books/${bookId}/numberOfPages"))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("Basic Y3VyYXRvcjpjdXJhdG9y"))
                    .withHeader("X-Correlation-ID", equalTo(correlationId))
                    .withRequestBody(equalToJson(
                            """{ "numberOfPages": 256 }"""
                    )))
        }

        @RecordLoggers(LibraryAccessor::class)
        @Test fun `logs successful requests`(wireMock: WireMockServer, log: LogRecord): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/numberOfPages"))
                    .willReturn(aResponse().withStatus(200)))
            accessor.updateNumberOfPages(bookId, numberOfPages)
            assertThat(log.messages)
                    .contains("successfully updated number of pages of book [$bookId] to [$numberOfPages]")
        }

        @RecordLoggers(LibraryAccessor::class)
        @ValueSource(ints = [400, 401, 403, 404, 500])
        @ParameterizedTest fun `logs failed requests`(status: Int, wireMock: WireMockServer, log: LogRecord): Unit = with(wireMock) {
            givenThat(put(urlEqualTo("/api/books/${bookId}/numberOfPages"))
                    .willReturn(aResponse().withStatus(status)))
            accessor.updateNumberOfPages(bookId, numberOfPages)
            assertThat(log.messages)
                    .contains("failed to update number of pages of book [$bookId] because of an error:")
        }

    }

}