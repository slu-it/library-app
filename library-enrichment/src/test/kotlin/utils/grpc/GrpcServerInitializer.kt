package utils.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

lateinit var grpcServer: Server
const val SERVER_PORT_TEST=50059

class GrpcServerInitializer(
) : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        grpcServer = ServerBuilder.forPort(SERVER_PORT_TEST)
            .addService(UpdateBookService())
            .build()

        grpcServer.start()
    }
}
