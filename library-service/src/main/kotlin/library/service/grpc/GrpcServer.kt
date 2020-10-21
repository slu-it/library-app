package library.service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import library.service.logging.logger
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(GrpcServerSettings::class)
class GrpcServer(
    private val bookService: BookService,
    private val grpcServerSettings: GrpcServerSettings
) {
    private val log = GrpcServer::class.logger

    private val port = grpcServerSettings.port.toInt()

    fun init() = ServerBuilder.forPort(port)
        .addService(bookService)
        .build()

    fun start(server: Server) {
        server.start()
        log.info("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                log.info("*** shutting down gRPC server since JVM is shutting down")
                stop(server)
                log.info("*** server shut down")
            }
        )
    }

    /**
     * Waits for the grpcServer to be terminated.
     */
    fun blockUntilShutdown(server: Server) {
        server.awaitTermination()
    }

    /**
     * Gracefully shutdown of the grpcServer
     */
    private fun stop(server: Server) {
        server.shutdown()
    }
}