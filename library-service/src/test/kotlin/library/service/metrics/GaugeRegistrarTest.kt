package library.service.metrics

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.willReturn
import io.micrometer.core.instrument.Gauge
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
        with(gauge("library.books.total")) {
            assertThat(value()).isEqualTo(42.0)
        }
    }

    @Test fun `number of borrowed books`() {
        given { repository.countByBorrowedNotNull() } willReturn { 42 }
        with(gauge("library.books.borrowed")) {
            assertThat(value()).isEqualTo(42.0)
        }
    }

    fun gauge(name: String): Gauge = registry.meters.single { it.id.name == name } as Gauge

}