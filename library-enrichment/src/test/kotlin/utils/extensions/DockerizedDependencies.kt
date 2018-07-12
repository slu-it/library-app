package utils.extensions

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue

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

class RabbitMqContainer : Container("rabbitmq:3.6") {
    init {
        setWaitStrategy(LogMessageWaitStrategy()
                .withRegEx(".*Server startup complete.*\n")
                .withStartupTimeout(Duration.ofSeconds(30)))
        addExposedPort(5672)
    }
}