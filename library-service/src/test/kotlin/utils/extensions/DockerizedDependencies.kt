package utils.extensions

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer

class MongoDBExtension : DockerizedDependencyExtension(
        image = "mongo:latest",
        containerCustomizer = {
            portBindings.add("27017:27017")
        }
)

class RabbitMQExtension : DockerizedDependencyExtension(
        image = "rabbitmq:3.6",
        containerCustomizer = {
            portBindings.add("5672:5672")
        },
        delay = 5_000
)

abstract class DockerizedDependencyExtension(
        image: String,
        containerCustomizer: Container.() -> Unit,
        private val delay: Long = 0
) : BeforeAllCallback, AfterAllCallback {

    private val container = Container(image).apply(containerCustomizer)

    override fun beforeAll(context: ExtensionContext) {
        if (isTopClassContext(context) && !container.isRunning) {
            container.start()
            Thread.sleep(delay)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        if (isTopClassContext(context) && container.isRunning) {
            container.stop()
        }
    }

    private fun isTopClassContext(context: ExtensionContext) =
            context.parent.orElse(null) == context.root

}

class Container(image: String) : GenericContainer<Container>(image)