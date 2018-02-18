package library.enrichment.messaging

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class ProcessedMessagesCounter(
        private val meterRegistry: MeterRegistry
) {

    fun increment() = counter().increment()

    val total: Long
        get() = counter().count().toLong()

    private fun counter(): Counter = meterRegistry.counter("messages.processed")

}