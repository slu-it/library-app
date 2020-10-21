package library.createbookclient.grpc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("grpc.server")
class GrpcServerSettings {
    lateinit var address: String
    lateinit var port: String
}