package library.service.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import library.service.database.BookRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class GaugeRegistrarTest {

    val registry = SimpleMeterRegistry()
    val repository: BookRepository = mockk()
    val cut = GaugeRegistrar(registry, repository)

    @BeforeEach fun init() = cut.registerGauges()

    @Test fun `total number of books`() {
        every { repository.count() } returns 42
        with(gauge("library.books.total")) {
            assertThat(value()).isEqualTo(42.0)
        }
    }

    @Test fun `number of borrowed books`() {
        every { repository.countByBorrowedNotNull() } returns 42
        with(gauge("library.books.borrowed")) {
            assertThat(value()).isEqualTo(42.0)
        }
    }

    fun gauge(name: String): Gauge = registry.meters.single { it.id.name == name } as Gauge

}