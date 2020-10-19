package library.createbookclient.grpc

import io.grpc.ManagedChannel
import library.createbookclient.CreateBookGrpcKt
import library.createbookclient.CreateBookRequest
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.io.Closeable
import java.util.concurrent.*

@Component
class CreateBookConsumer(private val channel: ManagedChannel) : Closeable {
    private val log = KotlinLogging.logger {}

    private val stub = CreateBookGrpcKt.CreateBookCoroutineStub(channel)

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