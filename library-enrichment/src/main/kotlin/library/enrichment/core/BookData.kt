package library.enrichment.core


data class BookData(
        val authors: List<String> = emptyList(),
        val numberOfPages: Int? = null
)