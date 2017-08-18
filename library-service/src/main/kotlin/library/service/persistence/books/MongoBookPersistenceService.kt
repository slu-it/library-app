package library.service.persistence.books

import library.service.business.books.BookPersistenceService
import library.service.business.books.domain.Book
import library.service.business.books.domain.PersistedBook
import library.service.business.books.domain.types.BorrowedState
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
@LogMethodEntryAndExit
class MongoBookPersistenceService(
        private val repository: BookRepository
) : BookPersistenceService {

    override fun create(book: Book): PersistedBook {
        val document = BookDocument().apply {
            id = UUID.randomUUID()
            isbn = book.isbn.value
            title = book.title.value
        }
        val createdDocument = repository.save(document)
        return toPersistedBook(createdDocument)
    }

    override fun update(persistedBook: PersistedBook): PersistedBook {
        val book = persistedBook.book

        val document = repository.findOne(persistedBook.id)!!
        document.isbn = book.isbn.value
        document.title = book.title.value
        document.borrowed = persistedBook.borrowed?.let {
            BookDocument.Borrowed(
                    by = it.by.value,
                    on = it.on.toString()
            )
        }

        val updatedDocument = repository.save(document)
        return toPersistedBook(updatedDocument)
    }

    override fun delete(persistedBook: PersistedBook) {
        repository.delete(persistedBook.id)
    }

    override fun findById(id: UUID): PersistedBook? {
        return repository.findOne(id)?.let(this::toPersistedBook)
    }

    override fun findAll(): List<PersistedBook> {
        return repository.findAll().map(this::toPersistedBook)
    }

    private fun toPersistedBook(document: BookDocument): PersistedBook {
        val id = document.id!!
        val book = Book(Isbn13(document.isbn!!), Title(document.title!!))
        val borrowedState = toBorrowedState(document.borrowed)
        return PersistedBook(id, book, borrowedState)
    }

    private fun toBorrowedState(borrowed: BookDocument.Borrowed?): BorrowedState? {
        return borrowed?.let {
            val by = Borrower(it.by!!)
            val on = OffsetDateTime.parse(it.on!!)
            BorrowedState(by, on)
        }
    }

}