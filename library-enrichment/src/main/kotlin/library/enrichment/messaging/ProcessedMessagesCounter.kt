package library.enrichment.messaging

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.stereotype.Component

@Component
class ProcessedMessagesCounter : MeterBinder {

    private lateinit var counter: Counter

    val total: Long
        get() = counter.count().toLong()

    fun increment(): Unit = counter.increment()

    override fun bindTo(registry: MeterRegistry) {
        counter = registry.counter("messages.processed")
    }

}