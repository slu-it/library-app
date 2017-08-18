package library.service.persistence.books

import library.service.business.books.BookPersistenceService
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.BookEntity
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

    override fun create(book: Book): BookEntity {
        val document = BookDocument().apply {
            id = UUID.randomUUID()
            isbn = book.isbn.value
            title = book.title.value
        }
        val createdDocument = repository.save(document)
        return toEntity(createdDocument)
    }

    override fun update(bookEntity: BookEntity): BookEntity {
        val book = bookEntity.book

        val document = repository.findOne(bookEntity.id)!!
        document.isbn = book.isbn.value
        document.title = book.title.value
        document.borrowed = bookEntity.borrowed?.let {
            BookDocument.Borrowed(
                    by = it.by.value,
                    on = it.on.toString()
            )
        }

        val updatedDocument = repository.save(document)
        return toEntity(updatedDocument)
    }

    override fun delete(bookEntity: BookEntity) {
        repository.delete(bookEntity.id)
    }

    override fun findById(id: UUID): BookEntity? {
        return repository.findOne(id)?.let(this::toEntity)
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