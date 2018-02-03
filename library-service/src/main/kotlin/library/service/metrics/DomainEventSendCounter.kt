package library.service.metrics

import io.micrometer.core.instrument.MeterRegistry
import library.service.business.events.DomainEvent
import org.springframework.stereotype.Component

@Component
class DomainEventSendCounter(
        private val meterRegistry: MeterRegistry
) {
    fun increment(event: DomainEvent) {
        meterRegistry.counter("library.events.send", "type", event.type).increment()
    }
}