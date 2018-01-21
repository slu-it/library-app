package library.enrichment.gateways.library

import feign.FeignException
import library.enrichment.core.Library
import library.enrichment.logging.logger
import org.springframework.stereotype.Component

@Component
class LibraryAccessor(
        private val libraryClient: LibraryClient
) : Library {

    private val log = LibraryAccessor::class.logger

    override fun updateAuthors(bookId: String, authors: List<String>) {
        try {
            libraryClient.updateAuthors(bookId, UpdateAuthors(authors))
            log.debug("successfully updated authors of book [{}] to {}", bookId, authors)
        } catch (e: FeignException) {
            log.error("failed to update authors of book [{}] because of an error:", bookId, e)
        }
    }

    override fun updateNumberOfPages(bookId: String, numberOfPages: Int) {
        try {
            libraryClient.updateNumberOfPages(bookId, UpdateNumberOfPages(numberOfPages))
            log.debug("successfully updated number of pages of book [{}] to [{}]", bookId, numberOfPages)
        } catch (e: FeignException) {
            log.error("failed to update number of pages of book [{}] because of an error:", bookId, e)
        }
    }

}