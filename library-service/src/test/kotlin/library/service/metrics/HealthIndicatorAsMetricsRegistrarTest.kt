package library.service.metrics

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import utils.classification.UnitTest


@UnitTest
internal class HealthIndicatorAsMetricsRegistrarTest {

    val meterRegistry = SimpleMeterRegistry()
    val healthIndicator = listOf(UpHealthIndicator(), DownHealthIndicator(), OutOfServiceHealthIndicator(), UnknownHealthIndicator())
    val cut = HealthIndicatorAsMetricsRegistrar(meterRegistry, healthIndicator)

    @BeforeEach fun init() {
        cut.register()
    }

    @CsvSource("up, 1.00", "down, 0.00", "outofservice, 0.00", "unknown, 0.00")
    @ParameterizedTest fun `health indicators are registered as gauges`(name: String, expectedValue: Double) {
        val value = meterRegistry.find("health.indicators.$name").gauge()?.value()
        assertThat(value).isEqualTo(expectedValue)
    }

    class UpHealthIndicator : HealthIndicator {
        override fun health(): Health = Health.up().build()
    }

    class DownHealthIndicator : HealthIndicator {
        override fun health(): Health = Health.down().build()
    }

    class OutOfServiceHealthIndicator : HealthIndicator {
        override fun health(): Health = Health.outOfService().build()
    }

    class UnknownHealthIndicator : HealthIndicator {
        override fun health(): Health = Health.unknown().build()
    }

}