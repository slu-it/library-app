package library.enrichment.gateways.openlibrary

import com.fasterxml.jackson.databind.JsonNode
import feign.FeignException
import library.enrichment.core.BookData
import library.enrichment.core.BookDataSource
import library.enrichment.logging.logger
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

/**
 * A [BookDataSource] using `openlibrary.org` as its source of information.
 */
@Service
@Order(2)
class OpenLibraryAccessor(
        private val client: OpenLibraryClient
) : BookDataSource {

    private val log = OpenLibraryAccessor::class.logger

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