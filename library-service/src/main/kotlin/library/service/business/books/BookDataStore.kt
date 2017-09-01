package library.service.business.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.BookId
import java.util.*

/**
 * Interface defining all methods which need to be implemented by a data store
 * in order to handle the persistence of books.
 */
interface BookDataStore {

    /**
     * Creates a new [BookRecord] based on the given [Book].
     *
     * Implementations need to generate an unique ID for the new [BookRecord]
     * and store it in some kind of data store in order to make it retrievable
     * by functions like [findById] or [findAll] at a later point.
     *
     * @param book the [Book] to create
     * @return the created [BookRecord]
     */
    fun create(book: Book): BookRecord

    /**
     * Updates the given [BookRecord] in the data store.
     *
     * All contained data should be used to override whatever is currently
     * persisted in the data store. The only thing which is not allowed to
     * be changed is the book's ID.
     *
     * @param bookRecord the [BookRecord] to update
     * @return the updated [BookRecord]
     */
    fun update(bookRecord: BookRecord): BookRecord

    /**
     * Deletes the given [BookRecord] from the data store.
     *
     * @param bookRecord the [BookRecord] to delete
     */
    fun delete(bookRecord: BookRecord)

    /**
     * Tries to find a [BookRecord] by its unique ID.
     *
     * @param id the book's [UUID]
     * @return the found [BookRecord] - might be `null`!
     */
    fun findById(id: BookId): BookRecord?

    /**
     * Finds all [BookRecord] currently present in the data store and returns
     * them as a list.
     *
     * @return the found [BookRecord] as a list
     */
    fun findAll(): List<BookRecord>

}