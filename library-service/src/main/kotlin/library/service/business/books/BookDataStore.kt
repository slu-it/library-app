package library.service.business.books

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.BookId
import java.util.*

/**
 * Interface defining all methods which need to be implemented by a data store
 * in order to handle the persistence of books.
 */
interface BookDataStore {

    /**
     * Creates a new [BookEntity] based on the given [Book].
     *
     * Implementations need to generate an unique ID for the new [BookEntity]
     * and store it in some kind of data store in order to make it retrievable
     * by functions like [findById] or [findAll] at a later point.
     *
     * @param book the [Book] to create
     * @return the created [BookEntity]
     */
    fun create(book: Book): BookEntity

    /**
     * Updates the given [BookEntity] in the data store.
     *
     * All contained data should be used to override whatever is currently
     * persisted in the data store. The only thing which is not allowed to
     * be changed is the book's ID.
     *
     * @param bookEntity the [BookEntity] to update
     * @return the updated [BookEntity]
     */
    fun update(bookEntity: BookEntity): BookEntity

    /**
     * Deletes the given [BookEntity] from the data store.
     *
     * @param bookEntity the [BookEntity] to delete
     */
    fun delete(bookEntity: BookEntity)

    /**
     * Tries to find a [BookEntity] by its unique ID.
     *
     * @param id the book's [UUID]
     * @return the found [BookEntity] - might be `null`!
     */
    fun findById(id: BookId): BookEntity?

    /**
     * Finds all [BookEntity] currently present in the data store and returns
     * them as a list.
     *
     * @return the found [BookEntity] as a list
     */
    fun findAll(): List<BookEntity>

}