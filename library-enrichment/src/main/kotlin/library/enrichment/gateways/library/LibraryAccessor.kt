package library.enrichment.gateways.library

import feign.FeignException
import library.enrichment.core.Library
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

/**
 * This implementation of the [Library] interface uses a Feign client to
 * communicate with the main library service.
 *
 * @see Library
 * @see LibraryClient
 */
@Service
class LibraryAccessor(
        private val libraryClient: LibraryClient
) : Library {

    private val log = logger {}

    override fun updateAuthors(bookId: String, authors: List<String>) = try {
        libraryClient.updateAuthors(bookId, UpdateAuthors(authors))
        log.debug { "successfully updated authors of book [$bookId] to $authors" }
    } catch (e: FeignException) {
        log.error(e) { "failed to update authors of book [$bookId] because of an error:" }
    }

    override fun updateNumberOfPages(bookId: String, numberOfPages: Int) = try {
        libraryClient.updateNumberOfPages(bookId, UpdateNumberOfPages(numberOfPages))
        log.debug { "successfully updated number of pages of book [$bookId] to [$numberOfPages]" }
    } catch (e: FeignException) {
        log.error(e) { "failed to update number of pages of book [$bookId] because of an error:" }
    }

}