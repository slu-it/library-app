package library.service.persistence.books

import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.common.logging.LogMethodEntryAndExit
import library.service.persistence.books.BookDocument.BorrowedState
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
                title = book.title.toString()
        )
        val createdDocument = repository.save(document)
        return toEntity(createdDocument)
    }

    override fun update(bookRecord: BookRecord): BookRecord {
        val book = bookRecord.book
        val bookId = bookRecord.id.toUuid()
        val bookState = bookRecord.state

        val document = repository.findById(bookId).get()

        document.isbn = book.isbn.toString()
        document.title = book.title.toString()
        document.borrowed = when (bookState) {
            is Borrowed -> {
                val by = bookState.by.toString()
                val on = bookState.on.withOffsetSameInstant(ZoneOffset.UTC).toString()
                BorrowedState(by, on)
            }
            is Available -> null
        }

        val updatedDocument = repository.save(document)
        return toEntity(updatedDocument)
    }

    override fun delete(bookRecord: BookRecord) {
        val bookId = bookRecord.id.toUuid()
        repository.deleteById(bookId)
    }

    override fun findById(id: BookId): BookRecord? {
        val bookId = id.toUuid()
        return repository.findById(bookId)
                .map(this::toEntity)
                .orElse(null)
    }

    override fun findAll(): List<BookRecord> {
        return repository.findAll()
                .map(this::toEntity)
    }

    private fun toEntity(document: BookDocument): BookRecord {
        val id = BookId(document.id!!)

        val isbn = Isbn13(document.isbn!!)
        val title = Title(document.title!!)
        val book = Book(isbn, title)

        val borrowedState = document.borrowed
        val state = if (borrowedState != null) Borrowed(borrowedState) else Available

        return BookRecord(id, book, state)
    }

    private fun Borrowed(borrowed: BorrowedState): Borrowed {
        return Borrowed(Borrower(borrowed.by!!), OffsetDateTime.parse(borrowed.on!!))
    }

}