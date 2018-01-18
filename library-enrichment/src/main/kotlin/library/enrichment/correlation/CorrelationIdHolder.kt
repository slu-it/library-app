package library.enrichment.correlation

import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * Component responsible for holding the currently set [CorrelationId].
 *
 * The correlation ID is a unique string marking logs, exceptions etc as
 * belonging to the same logical operation. It is usually set by the
 * [CorrelationIdServletFilter] as part of the service's HTTP request and
 * response handling or by the
 */
@Component
class CorrelationIdHolder {

    private val correlationIdProperty = "correlationId"

    fun remove() {
        MDC.remove(correlationIdProperty)
    }

    fun set(correlationId: String) {
        MDC.put(correlationIdProperty, correlationId)
    }

    fun get(): String? {
        return MDC.get(correlationIdProperty)
    }

}