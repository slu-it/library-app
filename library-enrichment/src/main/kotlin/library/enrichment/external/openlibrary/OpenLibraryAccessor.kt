package library.enrichment.external.openlibrary

import com.fasterxml.jackson.databind.JsonNode
import feign.FeignException
import library.enrichment.common.logging.logger
import library.enrichment.external.BookData
import library.enrichment.external.BookDataSource
import org.springframework.stereotype.Service

/**
 * A [BookDataSource] using `openlibrary.org` as its source of information.
 */
@Service
class OpenLibraryAccessor(
        private val client: OpenLibraryClient
) : BookDataSource {

    private val log = OpenLibraryAccessor::class.logger()

    override fun getBookData(isbn: String): BookData? {
        try {
            val searchResult = client.searchBooks(isbn)
            val bookResult = searchResult.get("ISBN:$isbn")
            if (bookResult != null) {
                return extractBookData(bookResult, isbn)
            }
        } catch (e: FeignException) {
            handleException(e)
        }
        return null
    }

    private fun extractBookData(resultNode: JsonNode, isbn: String): BookData {
        val title = resultNode.get("title")?.asText()
        val authors = resultNode.get("authors")?.map { it.get("name").asText() } ?: emptyList()
        val numberOfPages = resultNode.get("number_of_pages")?.asInt()
        return BookData(
                isbn = isbn,
                title = title,
                authors = authors,
                numberOfPages = numberOfPages
        )
    }

    private fun handleException(e: FeignException) {
        when (e.status()) {
            in 500..599 -> log.warn("Could not retrieve book data from openlibrary.org because of an error on their end:", e)
            else -> log.error("Could not retrieve book data from openlibrary.org because of an error on our end:", e)
        }
    }

}