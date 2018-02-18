package library.enrichment.messaging

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class ProcessedMessagesCounterTest {

    val registry = SimpleMeterRegistry()
    val cut = ProcessedMessagesCounter(registry)

    @Test fun `counter can be incremented`() {
        cut.increment()
        assertThat(currentCount()).isEqualTo(1)
        cut.increment()
        assertThat(currentCount()).isEqualTo(2)
    }

    @Test fun `counter total can be read`() {
        cut.increment()
        cut.increment()
        assertThat(cut.total).isEqualTo(2)
    }

    fun currentCount() = registry.counter("messages.processed").count().toLong()

}