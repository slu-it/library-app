package library.service.metrics

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import library.service.business.events.DomainEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class DomainEventSendCounterTest {

    val registry = SimpleMeterRegistry()
    val cut = DomainEventSendCounter(registry)

    @Test fun `events are counted by their type`() {
        val firstEvent = SomeDomainEvent(type = "first-type")
        val secondEvent = SomeDomainEvent(type = "second-type")

        cut.increment(firstEvent)
        cut.increment(secondEvent)
        cut.increment(secondEvent)

        assertThat(counter("first-type").count()).isEqualTo(1.0)
        assertThat(counter("second-type").count()).isEqualTo(2.0)
    }

    fun counter(type: String) = registry.counter("library.events.send", "type", type)

    data class SomeDomainEvent(
            override val type: String,
            override val id: String = "",
            override val timestamp: String = ""
    ) : DomainEvent

}