package test.extensions

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(DockerizedMongoDBExtension::class)
annotation class UseDockerToRunMongoDB

private class DockerizedMongoDBExtension : DockerizedDependencyExtension("docker-mongodb.yml")

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(DockerizedRabbitMQExtension::class)
annotation class UseDockerToRunRabbitMQ

private class DockerizedRabbitMQExtension : DockerizedDependencyExtension("docker-rabbitmq.yml")

private abstract class DockerizedDependencyExtension(
        dockerComposeFile: String
) : BeforeAllCallback, AfterAllCallback {

    private val dockerCompose = "docker-compose -f src/test/resources/$dockerComposeFile"

    override fun beforeAll(context: ExtensionContext) {
        execute("$dockerCompose up")
    }

    override fun afterAll(context: ExtensionContext) {
        execute("$dockerCompose down").waitFor()
    }

    private fun execute(command: String): Process {
        val process = Runtime.getRuntime().exec(command)
        startAsDaemonThread { process.inputStream.streamToConsole() }
        startAsDaemonThread { process.errorStream.streamToConsole() }
        return process
    }

    private fun InputStream.streamToConsole() {
        BufferedReader(InputStreamReader(this)).use {
            it.lines().forEach { println(it) }
        }
    }

    private fun startAsDaemonThread(body: () -> Unit) {
        with(Thread { body() }) {
            isDaemon = true
            start()
        }
    }

}
