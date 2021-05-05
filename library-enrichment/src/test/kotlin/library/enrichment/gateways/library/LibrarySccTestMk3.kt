package library.enrichment.gateways.library

import library.enrichment.correlation.CorrelationId
import library.enrichment.correlation.CorrelationIdRequestInterceptor
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL

private const val CORRELATION_ID = "5d59f7da-f52f-46df-85c5-2d97b3b42aad"

@TestInstance(PER_CLASS)
class LibrarySccTestMk3 {

    private val settings = LibrarySettings()
    private val correlationId = CorrelationId().apply { setOrGenerate(CORRELATION_ID) }
    private val interceptor = CorrelationIdRequestInterceptor(correlationId)
    private val libraryClient = LibraryConfiguration().libraryClient(settings, interceptor)

    @JvmField
    @RegisterExtension
    val stubRunnerExtension: StubRunnerExtension = StubRunnerExtension()
        .downloadLatestStub("library.scc", "library-service", "stubs")
        .stubsMode(LOCAL)

    @BeforeEach
    fun setup() {
        correlationId.setOrGenerate(CORRELATION_ID)
        settings.url = "${stubRunnerExtension.findStubUrl("library-service")}"
    }

    @Test
    fun `update authors of a book`() {
        val bookId = "3c15641e-2598-41f5-9097-b37e2d768be5"

        libraryClient.updateAuthors(bookId, UpdateAuthors(listOf("J.R.R. Tolkien", "Jim Butcher")))
    }

}