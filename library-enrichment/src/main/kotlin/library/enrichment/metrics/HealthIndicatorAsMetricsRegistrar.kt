package library.enrichment.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
internal class HealthIndicatorAsMetricsRegistrar(
        private val registry: MeterRegistry,
        private val healthIndicators: List<HealthIndicator>
) {

    @PostConstruct
    fun register() = healthIndicators.forEach { indicator ->
        registry.gauge("health.indicators.${nameOf(indicator)}", indicator) {
            if (it.health().status == Status.UP) 1.00 else 0.00
        }
    }

    private fun nameOf(it: HealthIndicator) =
            it.javaClass.simpleName.substringBefore("HealthIndicator").toLowerCase()

}