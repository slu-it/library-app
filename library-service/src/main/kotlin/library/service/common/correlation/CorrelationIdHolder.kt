package library.service.common.correlation

import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class CorrelationIdHolder {

    internal val correlationIdProperty = "correlationId"

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