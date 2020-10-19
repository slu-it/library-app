package utils

import io.grpc.Server
import io.grpc.ServerBuilder
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

lateinit var grpcServer: Server

class GrpcServerInitializer(
) : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val port = 50052

        grpcServer = ServerBuilder.forPort(port)
            .addService(CreateBookService())
            .build()

        grpcServer.start()
    }
}
