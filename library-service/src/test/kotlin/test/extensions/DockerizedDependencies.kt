package test.extensions

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentLinkedQueue

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(DockerizedMongoDBExtension::class)
annotation class UseDockerToRunMongoDB

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(DockerizedRabbitMQExtension::class)
annotation class UseDockerToRunRabbitMQ

private class DockerizedMongoDBExtension : DockerizedDependencyExtension(
        dockerComposeFile = "docker-mongodb.yml",
        startupPhrase = "waiting for connections on port"
)

private class DockerizedRabbitMQExtension : DockerizedDependencyExtension(
        dockerComposeFile = "docker-rabbitmq.yml",
        startupPhrase = "started TCP Listener on"
)

private abstract class DockerizedDependencyExtension(
        dockerComposeFile: String,
        private val startupPhrase: String
) : BeforeAllCallback, AfterAllCallback {

    private val dockerCompose = "docker-compose -f src/test/resources/$dockerComposeFile"
    private val log = ConcurrentLinkedQueue<String>()

    override fun beforeAll(context: ExtensionContext) {
        execute("$dockerCompose up")
        if (!waitUntilServiceWasStarted()) {
            error("service did not start in time: ${javaClass.simpleName}")
        }
    }

    override fun afterAll(context: ExtensionContext) {
        execute("$dockerCompose down").waitFor()
    }

    private fun execute(command: String): Process {
        val process = Runtime.getRuntime().exec(command)
        startAsDaemonThread { process.inputStream.streamToLog() }
        startAsDaemonThread { process.errorStream.streamToLog() }
        return process
    }

    private fun startAsDaemonThread(body: () -> Unit) {
        with(Thread { body() }) {
            isDaemon = true
            start()
        }
    }

    private fun InputStream.streamToLog() {
        BufferedReader(InputStreamReader(this)).use {
            it.lines().forEach {
                println(it)
                log.add(it)
            }
        }
    }

    private fun waitUntilServiceWasStarted(): Boolean {
        val start = now()
        var started = false
        while (!started && (now() - start) < 10_000L) {
            while (log.peek() != null) {
                if (log.poll().contains(startupPhrase, true)) {
                    started = true
                }
            }
            sleep(100L)
        }
        return started
    }

    private fun now() = System.currentTimeMillis()

}
