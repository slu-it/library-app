package library.enrichment.gateways.library.grpc

import io.grpc.ManagedChannel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.IntegrationTest
import utils.grpc.GrpcServerInitializer
import utils.grpc.SERVER_PORT_TEST
import utils.grpc.grpcServer

@SpringBootTest(
    properties = ["grpc.server.port=$SERVER_PORT_TEST"]
)
@ContextConfiguration(
    classes = [LibraryClientIntTest.TestConfiguration::class],
    initializers = [GrpcServerInitializer::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
class LibraryClientIntTest {

    @ComponentScan(basePackageClasses = [LibraryClient::class])
    class TestConfiguration

    @Autowired
    private lateinit var channel: ManagedChannel

    @Autowired
    private lateinit var libraryClient: LibraryClient

    @AfterAll
    fun closeResources() {
        channel.shutdownNow()
        grpcServer.shutdownNow()
    }

    @RecordLoggers(LibraryClient::class)
    @Test
    fun `should update Book number of pages, given an exisiting book`(log: LogRecord) {
        val expectedLogMessage =
            "successfully updated number of pages of book [175c5a7e-dd91-4d42-8c0d-6a97d8755231] to [576]"

        libraryClient.updateNumberOfPages("175c5a7e-dd91-4d42-8c0d-6a97d8755231", 576)

        assertThat(log.messages).containsOnlyOnce(expectedLogMessage)
    }

    @RecordLoggers(LibraryClient::class)
    @Test
    fun `should update Book authors, given an existing book`(log: LogRecord) {
        val firstAuthor = "Robert C. Martin"
        val secondAuthor = "Dean Wampler"

        val expectedLogMessage =
            "successfully updated authors of book [175c5a7e-dd91-4d42-8c0d-6a97d8755231] to [Robert C. Martin, Dean Wampler]"

        libraryClient.updateAuthors("175c5a7e-dd91-4d42-8c0d-6a97d8755231", listOf(firstAuthor, secondAuthor))

        assertThat(log.messages).containsOnlyOnce(expectedLogMessage)
    }
}