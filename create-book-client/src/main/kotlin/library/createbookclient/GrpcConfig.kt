package library.createbookclient

import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcConfig {
    @Bean
    fun channel() = ManagedChannelBuilder
        .forAddress("localhost", 50052)//TODO Make configurable
        .usePlaintext()
        .build()
}
