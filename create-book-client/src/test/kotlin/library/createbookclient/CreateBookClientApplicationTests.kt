package library.createbookclient

import io.grpc.ManagedChannel
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import utils.GrpcServerInitializer
import utils.grpcServer

@SpringBootTest(
    args = ["HarryPotter,9783551557414"],
    properties = ["grpc.server.port=${utils.SERVER_PORT_TEST}"]
)
/**
 * GRPC Server has to be started before the request is being sent.
 */
@ContextConfiguration(initializers = [GrpcServerInitializer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateBookClientApplicationTests {

    @Autowired
    private lateinit var channel: ManagedChannel

    @AfterAll
    fun closeResources() {
        channel.shutdownNow()
        grpcServer.shutdownNow()
    }

    @Test
    fun contextLoads() {
    }
}


