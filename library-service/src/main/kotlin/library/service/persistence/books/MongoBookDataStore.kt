package library.service.persistence.books

import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Service
@LogMethodEntryAndExit
class MongoBookDataStore(
        private val repository: BookRepository
) : BookDataStore {

    override fun create(book: Book): BookRecord {
        val document = BookDocument(
                id = UUID.randomUUID(),
                isbn = book.isbn.toString(),
                title = book.title.toString(),
                borrowed = null
        )
        val createdDocument = repository.save(document)
        return toBookRecord(createdDocument)
    }

    override fun update(bookRecord: BookRecord): BookRecord {
        val book = bookRecord.book
        val bookId = bookRecord.id.toUuid()
        val bookState = bookRecord.state

        val originalDocument = repository.findById(bookId).get()
        val newDocument = originalDocument.copy(
                isbn = "${book.isbn}",
                title = "${book.title}",
                borrowed = when (bookState) {
                    is Available -> null
                    is Borrowed -> BorrowedState(
                            by = "${bookState.by}",
                            on = "${bookState.on.withOffsetSameInstant(ZoneOffset.UTC)}"
                    )
                }
        )

        val updatedDocument = repository.save(newDocument)
        return toBookRecord(updatedDocument)
    }

    override fun delete(bookRecord: BookRecord) {
        val bookId = bookRecord.id.toUuid()
        repository.deleteById(bookId)
    }

    override fun findById(id: BookId): BookRecord? {
        val bookId = id.toUuid()
        return repository.findById(bookId)
                .map(this::toBookRecord)
                .orElse(null)
    }

    override fun findAll(): List<BookRecord> {
        return repository.findAll()
                .map(this::toBookRecord)
    }

    private fun toBookRecord(document: BookDocument): BookRecord {
        val borrowed = document.borrowed
        return BookRecord(
                id = BookId(document.id!!),
                book = Book(
                        isbn = Isbn13(document.isbn),
                        title = Title(document.title)
                ),
                initialState = when (borrowed) {
                    null -> Available
                    else -> Borrowed(
                            by = Borrower(borrowed.by),
                            on = OffsetDateTime.parse(borrowed.on)
                    )
                }
        )
    }

}