package library.service.common.correlation

import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 * Component responsible for holding the currently set correlation ID.
 *
 * The correlation ID is a unique string marking logs, exceptions etc as
 * belonging to the same request. It is usually set by the
 * [CorrelationIdSettingFilter] as part of the service's HTTP request and
 * response handling.
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