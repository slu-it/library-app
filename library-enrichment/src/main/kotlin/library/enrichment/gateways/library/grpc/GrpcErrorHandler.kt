package library.enrichment.gateways.library.grpc

import io.grpc.Status
import io.grpc.Status.DEADLINE_EXCEEDED
import io.grpc.Status.INTERNAL
import io.grpc.Status.RESOURCE_EXHAUSTED
import io.grpc.Status.UNAVAILABLE
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.Status.UNKNOWN
import io.grpc.StatusException
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component

@Component
class GrpcErrorHandler {

    private val log = logger {}

    fun handleError(statusException: StatusException) {
        when (statusException.status) {
            DEADLINE_EXCEEDED, UNIMPLEMENTED,
            UNAVAILABLE, UNKNOWN,
            INTERNAL, RESOURCE_EXHAUSTED -> logError(statusException.status)
        }
    }

    private fun logError(status: Status) {
        log.error { "Operation has failed with: $status = [${status.cause}]" }
    }
}