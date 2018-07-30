package library.enrichment.core

import library.enrichment.logging.LogMethodEntryAndExit
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component

/**
 * This component is responsible for orchestrating the handling of a
 * [BookAddedEvent] occurrence.
 *
 * For each added book it will gather available data from various
 * [data sources][BookDataSource] and decide on which data to actually
 * use to update the book in the library.
 *
 * @see BookAddedEvent
 * @see BookDataSource
 * @see Library
 */
@Component
@LogMethodEntryAndExit
class BookAddedEventHandler(
        private val dataSources: List<BookDataSource>,
        private val library: Library
) {

    private val log = logger {}

    fun handle(event: BookAddedEvent) {
        log.info { "processing book added event: $event" }

        val dataSets = gatherBookDataFromSources(event.isbn)
        if (dataSets.isNotEmpty()) {
            log.debug { "found ${dataSets.size} data set(s) for ISBN [${event.isbn}]" }
            chooseAuthors(dataSets)?.let {
                log.debug { "chose $it as the best author(s), updating book record ..." }
                library.updateAuthors(event.bookId, it)
            }
            chooseNumberOfPages(dataSets)?.let {
                log.debug { "chose [$it] as the best number of pages, updating book record ..." }
                library.updateNumberOfPages(event.bookId, it)
            }
        } else {
            log.debug { "could not find any data sets for ISBN [${event.isbn}]" }
        }
    }

    private fun gatherBookDataFromSources(isbn: String): List<BookData> {
        return dataSources.mapNotNull {
            log.debug { "looking up book data using [$it]" }
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