package library.service.grpc

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * Starts the GRPC Server right after the application context is ready.
 */
@ConditionalOnProperty(
    prefix = "application.runner",
    value = ["enabled"],
    havingValue = "true"
)
@Service
class GrpcServerRunner(
    private val grpcServer: GrpcServer
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val server = grpcServer.init()
        grpcServer.start(server)
        grpcServer.blockUntilShutdown(server)
    }
}