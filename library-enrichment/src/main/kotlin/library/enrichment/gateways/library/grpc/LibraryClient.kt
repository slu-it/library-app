package library.enrichment.gateways.library.grpc

import io.grpc.ManagedChannel
import io.grpc.StatusException
import kotlinx.coroutines.runBlocking
import library.enrichment.core.Library
import library.enrichment.gateways.library.grpc.UpdateAuthorsRequest
import library.enrichment.gateways.library.grpc.UpdateBookGrpcKt
import library.enrichment.gateways.library.grpc.UpdateNumberOfPagesRequest

import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import java.io.Closeable
import java.util.concurrent.*

@Component("updateBookConsumer")
class LibraryClient(
    private val channel: ManagedChannel,
    private val errorHandler: GrpcErrorHandler
) : Library, Closeable {
    private val log = logger {}

    private val stub =
        UpdateBookGrpcKt.UpdateBookCoroutineStub(channel)

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
    } catch (e: StatusException) {
        errorHandler.handleError(e)
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
    } catch (e: StatusException) {
        errorHandler.handleError(e)
    }

    override fun close() {
        channel.shutdown().awaitTermination(10, TimeUnit.SECONDS)
    }
}