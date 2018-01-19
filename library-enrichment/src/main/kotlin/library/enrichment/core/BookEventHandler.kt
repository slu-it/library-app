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
            chooseAuthors(bookDataSets)?.let {
                library.updateAuthors(event.bookId, it)
            }
            chooseNumberOfPages(bookDataSets)?.let {
                library.updateNumberOfPages(event.bookId, it)
            }
        }
    }

    private fun gatherBookDataFromSources(isbn: String): List<BookData> {
        return dataSources.mapNotNull {
            log.debug("looking up book data using {}", it)
            it.getBookData(isbn)
        }
    }

    private fun chooseAuthors(dataSets: Iterable<BookData>) = dataSets
            .map { it.authors }
            .firstOrNull { it.isNotEmpty() }

    private fun chooseNumberOfPages(dataSets: Iterable<BookData>) = dataSets
            .mapNotNull { it.numberOfPages }
            .firstOrNull { it > 0 }

}