package library.service.grpc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("grpc.server")
class GrpcServerSettings {
    lateinit var port: String
}