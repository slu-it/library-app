package library.service.metrics

import io.micrometer.core.instrument.MeterRegistry
import library.service.database.BookRepository
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
internal class GaugeRegistrar(
        private val registry: MeterRegistry,
        private val repository: BookRepository
) {

    @PostConstruct
    fun registerGauges() {
        registry.gauge("library.books.total", repository) { it.count().toDouble() }
        registry.gauge("library.books.borrowed", repository) { it.countByBorrowedNotNull().toDouble() }
    }

}