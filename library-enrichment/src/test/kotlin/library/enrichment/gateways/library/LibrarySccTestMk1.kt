package library.enrichment.gateways.library

import library.enrichment.correlation.CorrelationId
import library.enrichment.correlation.CorrelationIdRequestInterceptor
import library.enrichment.gateways.library.LibrarySccTestMk1.CustomConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import utils.extensions.EnableSpringExtension

@SpringBootTest
@AutoConfigureStubRunner(
    stubsMode = LOCAL,
    ids = ["library.scc:library-service-stubs"]
)
@EnableSpringExtension
@ContextConfiguration(classes = [CustomConfiguration::class])
class LibrarySccTestMk1(
    @Autowired val libraryClient: LibraryClient,
    @Autowired val settings: LibrarySettings,
    @Autowired val correlationId: CorrelationId
) {

    @ComponentScan(basePackageClasses = [LibraryAccessor::class, CorrelationIdRequestInterceptor::class])
    class CustomConfiguration

    @StubRunnerPort("library-service-stubs")
    private var stubRunnerPort: Int? = null

    @BeforeEach
    fun setup() {
        require(stubRunnerPort != null)
        settings.url = "http://localhost:$stubRunnerPort"
        correlationId.setOrGenerate(CORRELATION_ID)
    }

    @Test
    fun `update authors of a book`() {
        val bookId = "3c15641e-2598-41f5-9097-b37e2d768be5"

        libraryClient.updateAuthors(bookId, UpdateAuthors(listOf("J.R.R. Tolkien", "Jim Butcher")))
    }

    companion object {
        const val CORRELATION_ID = "5d59f7da-f52f-46df-85c5-2d97b3b42aad"
    }

}