package library.enrichment.core

import library.enrichment.logging.LogMethodEntryAndExit
import library.enrichment.logging.logger
import org.springframework.stereotype.Component

@Component
@LogMethodEntryAndExit
class BookEventHandler(
        private val dataSources: List<BookDataSource>,
        private val library: Library
) {

    private val log = BookEventHandler::class.logger

    fun handle(event: BookAddedEvent) {
        log.info("processing book added event: {}", event)

        val bookDataSets = gatherBookDataFromSources(event.isbn)
        if (bookDataSets.isNotEmpty()) {
            val mergedUpdateData = merge(bookDataSets)
            library.updateBookData(event.bookId, mergedUpdateData)
        }
    }

    private fun gatherBookDataFromSources(isbn: String): List<BookData> {
        return dataSources.mapNotNull {
            log.debug("looking up book data using {}", it)
            it.getBookData(isbn)
        }
    }

    private fun merge(dataSets: List<BookData>) = BookData(
            authors = chooseAuthors(dataSets),
            numberOfPages = chooseNumberOfPages(dataSets)
    )

    private fun chooseAuthors(dataSets: Iterable<BookData>) = dataSets
            .map { it.authors }
            .firstOrNull { it.isNotEmpty() }
            ?: emptyList()

    private fun chooseNumberOfPages(dataSets: Iterable<BookData>) = dataSets
            .mapNotNull { it.numberOfPages }
            .firstOrNull { it > 0 }

}