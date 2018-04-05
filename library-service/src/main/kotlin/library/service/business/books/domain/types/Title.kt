package library.service.business.books.domain.types

/** The title of a book. */
data class Title(
        private val value: String
) {

    companion object {
        const val VALID_TITLE_PATTERN = """(?U)[\w $ASCII_SPECIAL_CHARACTERS]+"""
    }

    override fun toString(): String = value

}