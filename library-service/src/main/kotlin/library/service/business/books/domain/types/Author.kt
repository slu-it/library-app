package library.service.business.books.domain.types

/** The author of a book. */
data class Author(
        private val value: String
) {

    override fun toString(): String = value

}