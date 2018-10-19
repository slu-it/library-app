package utils.testconfiguration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("acceptance-test")
class AcceptanceTestConfiguration {

}