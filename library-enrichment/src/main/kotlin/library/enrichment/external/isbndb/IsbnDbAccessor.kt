package library.enrichment.external.isbndb

import com.fasterxml.jackson.databind.JsonNode
import feign.FeignException
import library.enrichment.common.logging.logger
import library.enrichment.external.BookData
import library.enrichment.external.BookDataSource
import library.enrichment.external.openlibrary.OpenLibraryClient
import org.springframework.stereotype.Service

/**
 * A [BookDataSource] using `isbndb.com` as its source of information.
 */
@Service
class IsbnDbAccessor(
        private val client: IsbnDbClient,
        private val settings: IsbnDbSettings
) : BookDataSource {

    private val log = IsbnDbAccessor::class.logger()

    override fun getBookData(isbn: String): BookData? {
        log.debug("Looking up ISBN {} on isbndb.com ...", isbn)
        try {
            val apiKey = settings.apiKey
            val data = client.searchBooks(apiKey, isbn)
                    ?.get("data")
                    ?.get(0)
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
            authors = get("author_data")
                    ?.map { it.get("name").asText() }
                    ?: emptyList()
    )

    private fun handleException(e: FeignException) = when (e.status()) {
        in 500..599 -> log.warn("Could not retrieve book data from isbndb.com because of an error on their end:", e)
        else -> log.error("Could not retrieve book data from isbndb.com because of an error on our end:", e)
    }

}