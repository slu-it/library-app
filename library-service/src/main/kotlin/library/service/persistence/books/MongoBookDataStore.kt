package library.service.persistence.books

import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Book
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

    override fun create(book: Book): BookEntity {
        val document = BookDocument(
                id = UUID.randomUUID(),
                isbn = book.isbn.value,
                title = book.title.value
        )
        val createdDocument = repository.save(document)
        return toEntity(createdDocument)
    }

    override fun update(bookEntity: BookEntity): BookEntity {
        val book = bookEntity.book
        val bookId = bookEntity.id
        val bookState = bookEntity.state

        val document = repository.findById(bookId).get()

        document.isbn = book.isbn.value
        document.title = book.title.value
        document.borrowed = when (bookState) {
            is Borrowed -> {
                val by = bookState.by.value
                val on = bookState.on.withOffsetSameInstant(ZoneOffset.UTC).toString()
                BorrowedState(by, on)
            }
            is Available -> null
        }

        val updatedDocument = repository.save(document)
        return toEntity(updatedDocument)
    }

    override fun delete(bookEntity: BookEntity) {
        repository.deleteById(bookEntity.id)
    }

    override fun findById(id: UUID): BookEntity? {
        return repository.findById(id).map(this::toEntity).orElse(null)
    }

    override fun findAll(): List<BookEntity> {
        return repository.findAll().map(this::toEntity)
    }

    private fun toEntity(document: BookDocument): BookEntity {
        val id = document.id!!

        val isbn = Isbn13(document.isbn!!)
        val title = Title(document.title!!)
        val book = Book(isbn, title)

        val bookEntity = BookEntity(id, book)

        document.borrowed?.let {
            val by = Borrower(it.by!!)
            val on = OffsetDateTime.parse(it.on!!)
            bookEntity.borrow(by, on)
        }

        return bookEntity
    }

}