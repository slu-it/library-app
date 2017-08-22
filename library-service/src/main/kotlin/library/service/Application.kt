package library.service

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.time.Clock

@SpringBootApplication
class Application {

    @Bean
    fun clock(): Clock = Clock.systemUTC()

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
