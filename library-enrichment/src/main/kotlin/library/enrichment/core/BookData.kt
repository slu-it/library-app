package library.enrichment.core


/**
 * Immutable composite of data which can be gathered from a [BookDataSource].
 */
data class BookData(
        val authors: List<String> = emptyList(),
        val numberOfPages: Int? = null
)