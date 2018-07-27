package utils.extensions

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration

class MongoDbExtension : DockerizedDependencyExtension(
        containerCreator = { MongoDbContainer() },
        port = 27017,
        portProperty = "MONGODB_PORT"
)

class RabbitMqExtension : DockerizedDependencyExtension(
        containerCreator = { RabbitMqContainer() },
        port = 5672,
        portProperty = "RABBITMQ_PORT"
)

open class DockerizedDependencyExtension(
        containerCreator: () -> Container,
        private val port: Int,
        private val portProperty: String
) : BeforeAllCallback, AfterAllCallback {

    private val container = containerCreator()

    override fun beforeAll(context: ExtensionContext) {
        if (isTopClassContext(context) && !container.isRunning) {
            container.start()
            System.setProperty(portProperty, "${container.getMappedPort(port)}")
        }
    }

    override fun afterAll(context: ExtensionContext) {
        if (isTopClassContext(context) && container.isRunning) {
            container.stop()
        }
    }

    private fun isTopClassContext(context: ExtensionContext) = context.parent.orElse(null) == context.root

}

sealed class Container(image: String) : GenericContainer<Container>(image)

class MongoDbContainer : Container("mongo:3.5") {
    init {
        addExposedPort(27017)
    }
}

class RabbitMqContainer : Container("rabbitmq:3.6") {
    init {
        setWaitStrategy(LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30)))
        addExposedPort(5672)
    }
}