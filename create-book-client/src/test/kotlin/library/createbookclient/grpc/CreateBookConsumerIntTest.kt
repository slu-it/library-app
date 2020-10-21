package library.createbookclient.grpc

import io.grpc.ManagedChannel
import kotlinx.coroutines.runBlocking
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
import utils.GrpcServerInitializer
import utils.SERVER_PORT_TEST
import utils.grpcServer

@SpringBootTest(
    properties = ["grpc.server.port=$SERVER_PORT_TEST"]
)
@ContextConfiguration(
    classes = [CreateBookConsumerIntTest.TestConfiguration::class],
    initializers = [GrpcServerInitializer::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBookConsumerIntTest {

    @ComponentScan(basePackageClasses = [CreateBookConsumer::class])
    class TestConfiguration

    @Autowired
    private lateinit var channel: ManagedChannel

    @Autowired
    private lateinit var createBookConsumer: CreateBookConsumer

    @AfterAll
    fun closeResources() {
        channel.shutdownNow()
        grpcServer.shutdownNow()
    }

    @RecordLoggers(CreateBookConsumer::class)
    @Test
    fun `should craete Book and log its title and isbn, given a valid title and isbn`(log: LogRecord) {
        val expectedLogMessage = "Created book with title=[The Lord of the Rings] and isbn=[9780261102385]"

        runBlocking {
            createBookConsumer.sendBook("9780261102385", "The Lord of the Rings")
        }
        assertThat(log.messages).containsOnlyOnce(expectedLogMessage)
    }
}
