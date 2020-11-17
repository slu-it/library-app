package library.enrichment.gateways.library.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GrpcServerSettings::class)
class GrpcConfig(
    private val grpcServerSettings: GrpcServerSettings
) {
    @Bean
    fun channel() = ManagedChannelBuilder
        .forAddress(grpcServerSettings.address, grpcServerSettings.port.toInt())
        .usePlaintext()
        .build()
}
