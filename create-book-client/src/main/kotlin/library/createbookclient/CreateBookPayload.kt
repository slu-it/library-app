package library.createbookclient

data class CreateBookPayload(
    val isbn: String,
    val title: String
) {
    init {
        check(title.isNotBlank()){"Title must not be empty"}
        check(isbn.matches(Regex(ISBN_13_REGEX_VALUE))) { "ISBN Format validation has failed" }
    }

    companion object {
        private const val ISBN_13_REGEX_VALUE = """(\d{3}-?)?\d{10}"""
    }
}