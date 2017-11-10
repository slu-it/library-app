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
        log.debug("Looking up ISBN {} on openlibrary.org ...", isbn)
        try {
            val data = client.searchBooks(isbn)
                    .get("ISBN:$isbn")
                    ?.extractBookData()
            log.debug("Found book data: {}", data)
            return data
        } catch (e: FeignException) {
            handleException(e)
        }
        return null
    }

    private fun JsonNode.extractBookData() = BookData(
            title = get("title")
                    ?.asText(),
            authors = get("authors")
                    ?.map { it.get("name").asText() }
                    ?: emptyList(),
            numberOfPages = get("number_of_pages")
                    ?.asInt()
    )

    private fun handleException(e: FeignException) = when (e.status()) {
        in 500..599 -> log.warn("Could not retrieve book data from openlibrary.org because of an error on their end:", e)
        else -> log.error("Could not retrieve book data from openlibrary.org because of an error on our end:", e)
    }

}