package library.enrichment.gateways.openlibrary

import com.fasterxml.jackson.databind.JsonNode
import feign.FeignException
import library.enrichment.core.BookData
import library.enrichment.core.BookDataSource
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

/**
 * A [BookDataSource] using `openlibrary.org` as its source of information.
 *
 * @see BookDataSource
 * @see OpenLibraryClient
 */
@Service
class OpenLibraryAccessor(
        private val client: OpenLibraryClient
) : BookDataSource {

    private val log = logger {}

    override fun getBookData(isbn: String): BookData? {
        log.debug { "looking up ISBN [$isbn] on openlibrary.org ..." }
        try {
            val data = client.searchBooks(isbn)
                    .get("ISBN:$isbn")
                    ?.extractBookData()
            log.debug { "found book data: $data" }
            return data
        } catch (e: FeignException) {
            handleException(e)
        }
        return null
    }

    private fun JsonNode.extractBookData(): BookData {
        val authors = get("authors")
                ?.map { it.get("name").asText() }
                ?: emptyList()
        val numberOfPages = get("number_of_pages")
                ?.asInt()
        return BookData(authors, numberOfPages)
    }

    private fun handleException(e: FeignException) = when (e.status()) {
        in 500..599 -> log.warn(e) { "could not retrieve book data from openlibrary.org because of an error on THEIR end:" }
        else -> log.error(e) { "could not retrieve book data from openlibrary.org because of an error on OUR end:" }
    }

}