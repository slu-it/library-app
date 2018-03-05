package library.service.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * This component exposes all available [HealthIndicator] beans as [Gauge]
 * metrics on a scale from `0.00` (DOWN) to `1.00` (UP).
 *
 * The metrics name is derived from the [HealthIndicator's][HealthIndicator] name:
 * `health.indicators.${name}`. Where the name is simple the [HealthIndicator]
 * class' name up to `HealthIndicator` in lower case.
 *
 * **Examples: **
 *
 * - `DataSourceHealthIndicator` would have the metrics name `health.indicators.datasource`
 * - `ElasticsearchHealthIndicator` would have the metrics name `health.indicators.elasticsearch`
 *
 * @see HealthIndicator
 * @see Gauge
 * @see MeterRegistry
 */
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