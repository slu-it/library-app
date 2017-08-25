package library.service.business.books.domain.types

import library.service.business.books.domain.BookEntity
import java.util.*

/** The unique ID of a [BookEntity]. */
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
            val uuid = UUID.fromString(value)
            return BookId(uuid)
        }

    }

}