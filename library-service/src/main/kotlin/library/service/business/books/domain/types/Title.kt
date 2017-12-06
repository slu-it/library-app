package library.service.business.books.domain.types

/** The title of a book. */
data class Title(
        private val value: String
) {

    override fun toString(): String = value

}