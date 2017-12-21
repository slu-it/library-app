package library.service.business.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.BookId
import java.util.*

/**
 * Interface defining all methods which need to be implemented by a data store
 * in order to handle the persistence of books.
 */
interface BookDataStore {

    /**
     * Creates or updates the given [BookRecord] in the data store.
     *
     * The previous existence of a record has to be verified by the caller.
     * This method will override any existing data based on the record's
     * [BookId]!
     *
     * @param bookRecord the [BookRecord] to create or update
     * @return the created / updated [BookRecord]
     */
    fun createOrUpdate(bookRecord: BookRecord): BookRecord

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

    /**
     * Checks if there exists a [BookRecord] for the given [BookId].
     *
     * @return `true` if a record exists, otherwise `false`
     */
    fun existsById(bookId: BookId): Boolean

}