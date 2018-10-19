package utils.testconfiguration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import utils.MutableClock

@Configuration
@Profile("test")
class BaseTestConfiguration {
    
    @Bean fun clock() = MutableClock()

}