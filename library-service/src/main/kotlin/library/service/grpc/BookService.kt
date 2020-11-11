package library.service.grpc

import com.google.protobuf.Empty
import kotlinx.coroutines.flow.Flow
import library.service.business.books.BookCollection
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Author
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.grpc.CreateBookGrpcKt.CreateBookCoroutineImplBase
import library.service.logging.logger
import library.service.security.SecurityCommons
import org.springframework.stereotype.Component
import java.util.*

@Component
class CreateBookService(
    private val collection: BookCollection,
    private val mapper: BookResponseMapper,
    private val securityCommons: SecurityCommons
) : CreateBookCoroutineImplBase() {

    private val log = CreateBookService::class.logger

    override suspend fun createBook(request: CreateBookRequest): BookResponse {
        try {
            log.info("Received book with ${request.title} and ${request.isbn}")
            val book = Book(
                isbn = Isbn13.parse(request.isbn),
                title = Title(request.title),
                authors = emptyList(),
                numberOfPages = null
            )
            val bookRecord = securityCommons.executeOperationAsCurator { collection.addBook(book) }
            val response = mapper.toBookResponse(bookRecord)
            log.info("Sending response with $response")
            return response
        } catch (e: Exception) {
            log.error("Grpc Response could not be sent due to error = [${e.message}]")
            log.debug("${e.printStackTrace()}")
            throw e
        }
    }
}

@Component
class GetBooksService(
    private val collection: BookCollection,
    private val mapper: BookResponseMapper,
    private val securityCommons: SecurityCommons
) : GetBooksGrpcKt.GetBooksCoroutineImplBase() {

    private val log = GetBooksService::class.logger

    override fun getBooks(request: Empty): Flow<BookResponse> {
        try {
            val bookRecords = securityCommons.executeOperationAsUser { collection.getAllBooks() }
            log.info("Sending response with $bookRecords")
            return mapper.toBooksResponse(bookRecords)
        } catch (e: Exception) {
            log.error("Grpc Response could not be sent due to error = [${e.message}]")
            log.debug("${e.printStackTrace()}")
            throw e
        }
    }
}

@Component
class UpdateBookService(
    private val collection: BookCollection,
    private val mapper: BookResponseMapper,
    private val securityCommons: SecurityCommons
) : UpdateBookGrpcKt.UpdateBookCoroutineImplBase() {

    private val log = UpdateBookService::class.logger

    override suspend fun updateAuthors(request: UpdateAuthorsRequest): BookResponse {
        try {
            val bookRecord = securityCommons.executeOperationAsCurator {
                collection.updateBook(BookId(UUID.fromString(request.bookId))) {
                    it.changeAuthors(request.authorsList.map { author -> Author(author) })
                }
            }
            log.info("Sending response with $bookRecord")
            return mapper.toBookResponse(bookRecord)
        } catch (e: Exception) {
            log.error("Grpc Response could not be sent due to error = [${e.message}]")
            log.debug("${e.printStackTrace()}")
            throw e
        }
    }

    override suspend fun updateNumberOfPages(request: UpdateNumberOfPagesRequest): BookResponse {
        try {
            val bookRecord = securityCommons.executeOperationAsCurator {
                collection.updateBook(BookId(UUID.fromString(request.bookId))) {
                    it.changeNumberOfPages(request.numberOfPages)
                }
            }
            log.info("Sending response with $bookRecord")
            return mapper.toBookResponse(bookRecord)
        } catch (e: Exception) {
            log.error("Grpc Response could not be sent due to error = [${e.message}]")
            log.debug("${e.printStackTrace()}")
            throw e
        }
    }
}