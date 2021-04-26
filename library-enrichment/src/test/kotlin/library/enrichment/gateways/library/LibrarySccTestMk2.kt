package library.enrichment.gateways.library

import library.enrichment.correlation.CorrelationId
import library.enrichment.gateways.library.LibrarySccTestMk1.CustomConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerExtension
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL
import org.springframework.test.context.ContextConfiguration
import utils.extensions.EnableSpringExtension

@SpringBootTest
@EnableSpringExtension
@ContextConfiguration(classes = [CustomConfiguration::class])
class LibrarySccTestMk2(
    @Autowired val libraryClient: LibraryClient,
    @Autowired val settings: LibrarySettings,
    @Autowired val correlationId: CorrelationId
) {

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

    companion object {
        const val CORRELATION_ID = "5d59f7da-f52f-46df-85c5-2d97b3b42aad"

        @JvmField
        @RegisterExtension
        val stubRunnerExtension: StubRunnerExtension = StubRunnerExtension()
            .downloadLatestStub("library.scc", "library-service", "stubs")
            .stubsMode(LOCAL)
    }

}