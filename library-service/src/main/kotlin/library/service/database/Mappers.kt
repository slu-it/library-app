package library.service.database

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.*
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

interface Mapper<in S : Any, out T : Any> {
    fun map(source: S): T
}

@Component
class BookRecordToDocumentMapper : Mapper<BookRecord, BookDocument> {

    override fun map(source: BookRecord): BookDocument {
        val bookState = source.state
        return BookDocument(
                id = source.id.toUuid(),
                isbn = "${source.book.isbn}",
                title = "${source.book.title}",
                authors = source.book.authors.map { it.toString() },
                numberOfPages = source.book.numberOfPages,
                borrowed = when (bookState) {
                    is Available -> null
                    is Borrowed -> BorrowedState(
                            by = "${bookState.by}",
                            on = "${bookState.on.withOffsetSameInstant(ZoneOffset.UTC)}"
                    )
                }
        )
    }

}

@Component
class BookDocumentToRecordMapper : Mapper<BookDocument, BookRecord> {

    override fun map(source: BookDocument): BookRecord {
        val borrowed = source.borrowed
        return BookRecord(
                id = BookId(source.id),
                book = Book(
                        isbn = Isbn13(source.isbn),
                        title = Title(source.title),
                        authors = source.authors?.map { Author(it) } ?: emptyList(),
                        numberOfPages = source.numberOfPages
                ),
                state = when (borrowed) {
                    null -> Available
                    else -> Borrowed(
                            by = Borrower(borrowed.by),
                            on = OffsetDateTime.parse(borrowed.on)
                    )
                }
        )
    }

}