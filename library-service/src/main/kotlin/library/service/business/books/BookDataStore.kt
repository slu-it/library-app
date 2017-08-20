package library.service.business.books

import library.service.business.books.domain.types.Book
import library.service.business.books.domain.BookEntity
import java.util.*

interface BookDataStore {

    fun create(book: Book): BookEntity

    fun update(bookEntity: BookEntity): BookEntity

    fun delete(bookEntity: BookEntity)

    fun findById(id: UUID): BookEntity?

    fun findAll(): List<BookEntity>

}