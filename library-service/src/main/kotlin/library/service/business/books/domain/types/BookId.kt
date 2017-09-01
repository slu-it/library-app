package library.service.business.books.domain.types

import library.service.business.books.domain.BookRecord
import library.service.business.exceptions.MalformedValueException
import java.util.*

/** The unique ID of a [BookRecord]. */
data class BookId(
        private val value: UUID
) {

    fun toUuid(): UUID = value
    override fun toString(): String = value.toString()

    companion object {

        fun generate(): BookId {
            val uuid = UUID.randomUUID()
            return BookId(uuid)
        }

        fun from(value: String): BookId {
            try {
                val uuid = UUID.fromString(value)
                return BookId(uuid)
            } catch (e: IllegalArgumentException) {
                throw NotAnUuidException(value)
            }
        }

    }

    class NotAnUuidException(value: String)
        : MalformedValueException("This is not a valid UUID: $value")

}