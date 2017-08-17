package library.service.business.books

import library.service.business.books.domain.Book
import library.service.business.books.domain.PersistedBook
import java.util.*

interface BookPersistenceService {

    fun create(book: Book): PersistedBook

    fun update(persistedBook: PersistedBook): PersistedBook

    fun delete(persistedBook: PersistedBook)

    fun findById(id: UUID): PersistedBook?

    fun findAll(): List<PersistedBook>

}