package library.enrichment.correlation

import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

/**
 * Component responsible for holding the currently set [CorrelationId].
 *
 * The correlation ID is a unique string marking logs, exceptions etc as
 * belonging to the same logical operation. It is set by all components
 * providing access to this application (eg. [CorrelationIdServletFilter],
 * [CorrelationIdMessageReceivedPostProcessor]).
 */
@Component
class CorrelationId {

    private val property = "correlationId"

    init {
        remove()
    }

    fun setOrGenerate(id: String?): String = (id ?: generate()).also { MDC.put(property, it) }
    fun get(): String = MDC.get(property) ?: setOrGenerate(null)
    fun remove(): Unit = MDC.remove(property)

    private fun generate() = UUID.randomUUID().toString()

}