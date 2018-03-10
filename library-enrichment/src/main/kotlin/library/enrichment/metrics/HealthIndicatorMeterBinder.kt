package library.enrichment.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Status
import org.springframework.stereotype.Component

/**
 * This [MeterBinder] exposes all available [HealthIndicator] beans as [Gauge]
 * meters on a scale from `-1.00` (DOWN) to `1.00` (UP).
 *
 * The meter name is derived from the [HealthIndicator's][HealthIndicator] name:
 * `health.indicators.${name}`. Where the name is simple the [HealthIndicator]
 * class' name up to `HealthIndicator` in lower case.
 *
 * **Examples: **
 *
 * - `DataSourceHealthIndicator` would have the metrics name `health.indicators.datasource`
 * - `ElasticsearchHealthIndicator` would have the metrics name `health.indicators.elasticsearch`
 *
 * @see MeterBinder
 * @see HealthIndicator
 * @see Gauge
 * @see MeterRegistry
 */
@Component
internal class HealthIndicatorMeterBinder(
        private val indicators: List<HealthIndicator>
) : MeterBinder {

    private val gaugeValueFunction: (HealthIndicator) -> Double = {
        when (it.health().status) {
            Status.UP -> 1.00
            Status.DOWN, Status.OUT_OF_SERVICE -> -1.00
            else -> 0.00
        }
    }

    override fun bindTo(registry: MeterRegistry) = indicators.forEach { indicator ->
        Gauge.builder(indicator.meterName, indicator, gaugeValueFunction)
                .description("Indicates if the ${indicator.name} is UP (1.00) or DOWN (-1.00).")
                .register(registry)
    }

    private val HealthIndicator.name: String
        get() = javaClass.simpleName
    private val HealthIndicator.meterName: String
        get() = "health.indicators." + name.substringBefore("HealthIndicator").toLowerCase()

}