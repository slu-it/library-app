package library.service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import library.service.logging.logger
import org.springframework.stereotype.Component

@Component
class GrpcServer(
    private val bookService: BookService
) {
    private val log = GrpcServer::class.logger

    private val port = 50052

    fun init() = ServerBuilder.forPort(port) //TODO: Make port configurable
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