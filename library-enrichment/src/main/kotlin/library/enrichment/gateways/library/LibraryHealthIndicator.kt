package library.enrichment.gateways.library

import feign.FeignException
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
internal class LibraryHealthIndicator(
        private val client: LibraryClient
) : HealthIndicator {

    override fun health(): Health = try {
        client.ping()
        Health.up().build()
    } catch (e: FeignException) {
        Health.down()
                .withDetail("status", e.status())
                .withException(e)
                .build()
    }

}