package library.service.metrics

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.willReturn
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import library.service.database.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class GaugeRegistrarTest {

    val registry = SimpleMeterRegistry()
    val repository: BookRepository = mock()
    val cut = GaugeRegistrar(registry, repository)

    @BeforeEach fun init() = cut.registerGauges()

    @Test fun `total number of books`() {
        given { repository.count() } willReturn { 42 }
        val total = registry.getGauge("library.books.total")
        assertThat(total.value()).isEqualTo(42.0)
    }

    @Test fun `number of borrowed books`() {
        given { repository.countByBorrowedNotNull() } willReturn { 42 }
        val total = registry.getGauge("library.books.borrowed")
        assertThat(total.value()).isEqualTo(42.0)
    }


}

private fun MeterRegistry.getGauge(name: String): Gauge = meters.single { it.id.name == name } as Gauge