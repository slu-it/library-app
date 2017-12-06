package library.service.business.books.domain.types

import library.service.business.books.domain.BookRecord
import library.service.business.exceptions.MalformedValueException
import java.util.*

/** The unique ID of a [BookRecord]. */
data class BookId(
        private val value: UUID
) {

    /** Returns the book ID as a [UUID]. */
    fun toUuid(): UUID = value

    /**
     * Returns the book ID as a `UUID` string.
     *
     * Example: `123e4567-e89b-12d3-a456-426655440000`
     */
    override fun toString(): String = value.toString()

    companion object {

        /** Generates a new unique random [BookId]. */
        fun generate(): BookId {
            val uuid = UUID.randomUUID()
            return BookId(uuid)
        }

        /**
         * Creates a [BookId] from the given string. The string must be formatted
         * as an `UUID`.
         *
         * Example: `123e4567-e89b-12d3-a456-426655440000`
         *
         * @throws NotABookIdException in case the given string does not comply
         *                             with the `UUID` format
         */
        fun from(value: String): BookId = try {
            BookId(UUID.fromString(value))
        } catch (e: IllegalArgumentException) {
            throw NotABookIdException(value)
        }

    }

    class NotABookIdException(value: String)
        : MalformedValueException("This is not a valid book ID: $value")

}