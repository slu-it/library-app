package library.service.business.books.domain.types

import library.service.business.books.domain.BookEntity
import java.util.*

/** The unique ID of a [BookEntity]. */
class BookId(
        private val value: UUID
) {

    fun toUuid(): UUID = value
    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BookId
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

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