package library.enrichment.external


data class BookData(
        val isbn: String,
        val title: String? = null,
        val authors: List<String> = emptyList(),
        val numberOfPages: Int? = null
)