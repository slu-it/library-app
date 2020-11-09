package library.enrichment.gateways.grpc

import io.grpc.ManagedChannel
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import library.enrichment.core.Library

import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import java.io.Closeable
import java.util.concurrent.*

@Component("updateBookConsumer")
class LibraryClient(private val channel: ManagedChannel) : Library, Closeable {
    private val log = logger {}

    private val stub = UpdateBookGrpcKt.UpdateBookCoroutineStub(channel)

    override fun updateAuthors(bookId: String, authors: List<String>) = try {
        runBlocking {
            stub.updateAuthors(
                UpdateAuthorsRequest.newBuilder().apply {
                    this.addAllAuthors(authors)
                    this.bookId = bookId
                }.build()
            )
        }
        log.debug { "successfully updated authors of book [$bookId] to $authors" }
    } catch (e: Exception) {
        log.error(e) { "failed to update authors of book [$bookId] because of an error: ${e.message}" }
    }

    override fun updateNumberOfPages(bookId: String, numberOfPages: Int) = try {
        runBlocking {
            stub.updateNumberOfPages(
                UpdateNumberOfPagesRequest.newBuilder().apply {
                    this.bookId = bookId
                    this.numberOfPages = numberOfPages
                }.build()
            )
        }
        log.info { "successfully updated number of pages of book [$bookId] to [$numberOfPages]" }
    } catch (e: StatusException) { //TODO: GRPC Proper exception handling
        log.error(e) { "failed to update number of pages of book [$bookId] because of an error:" }
    }

    override fun close() {
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
}