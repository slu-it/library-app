package library.service.grpc

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Available
import org.springframework.stereotype.Component
import library.service.business.books.domain.states.Borrowed as BorrowedState

/**
 * Component responsible for converting a [BookRecord] into a [CreateBookResponse].
 */
@Component
class CreateBookResponseMapper {

    fun toCreateBookResponse(bookRecord: BookRecord) = CreateBookResponse
        .newBuilder()
        .apply {
            this.isbn = bookRecord.book.isbn.toString()
            this.title = bookRecord.book.title.toString()
            this.addAllAuthors(bookRecord.book.authors.map { it.toString() }.toMutableList())
            this.numberOfPages = bookRecord.book.numberOfPages ?: 0
            this.borrowed = when (bookRecord.state) {
                is Available -> Borrowed.newBuilder().build()
                is BorrowedState -> Borrowed.newBuilder()
                    .setBy(bookRecord.state.by.toString())
                    .setOn(bookRecord.state.on.toString())
                    .build()
            }
        }
        .build()
}