package library.service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import org.springframework.stereotype.Component

@Component
class GrpcServer (
    private val bookService: BookService
) {
    fun init() = ServerBuilder.forPort(50052) //TODO: Make port configurable
        .addService(bookService)
        .build()

    fun start(server: Server){
        server.start()
        println("Server started, listening on 50052") //Replace with log
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop(server)
                println("*** server shut down")
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