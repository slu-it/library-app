package library.service.grpc

import library.service.business.books.BookCollection
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.grpc.CreateBookGrpcKt.CreateBookCoroutineImplBase
import org.springframework.stereotype.Component

@Component
class BookService(
    private val collection: BookCollection
) : CreateBookCoroutineImplBase() {

    override suspend fun createBook(request: CreateBookRequest): CreateBookResponse {
        println("Received book with ${request.title} and ${request.isbn}") //TODO: replace with log
        val book = Book(
            isbn = Isbn13.parse(request.isbn),
            title = Title(request.title),
            authors = emptyList(),
            numberOfPages = null
        )
        val bookRecord = collection.addBook(book)
        val response = CreateBookResponse
            .newBuilder()
            .apply {
                this.isbn = bookRecord.book.isbn.toString()
                this.title = bookRecord.book.title.toString()
                // this.authors = emptyArray<String>()
                this.numberOfPages = bookRecord.book.numberOfPages!!
                this.borrowed = null
            }
            .build()
        println("Sending response with $response")
        return response
    }
}