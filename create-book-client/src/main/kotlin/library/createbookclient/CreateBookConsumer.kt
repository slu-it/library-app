package library.createbookclient

import io.grpc.ManagedChannel
import org.springframework.stereotype.Component
import java.io.Closeable
import java.util.concurrent.*
import library.createbookclient.CreateBookGrpcKt.CreateBookCoroutineStub
import mu.KotlinLogging

@Component
class CreateBookConsumer(private val channel: ManagedChannel) : Closeable {
    private val log = KotlinLogging.logger {}

    private val stub = CreateBookCoroutineStub(channel)

    suspend fun sendBook(isbn: String, title: String) {
        log.info { "Creating book with title = [$title] and isbn = [$isbn]" }
        val request = CreateBookRequest.newBuilder()
            .setIsbn(isbn)
            .setTitle(title)
            .build()
        val response = stub.createBook(request)
        log.info { "Created book with title=[${response.title}] and isbn=[${response.isbn}]" }
    }

    override fun close() {
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
}