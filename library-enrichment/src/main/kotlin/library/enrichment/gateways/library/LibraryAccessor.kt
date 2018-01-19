package library.enrichment.gateways.library

import library.enrichment.core.Library
import org.springframework.stereotype.Component

@Component
class LibraryAccessor(
        private val libraryClient: LibraryClient
) : Library {

    override fun updateAuthors(bookId: String, authors: List<String>) {
        libraryClient.updateAuthors(bookId, UpdateAuthors(authors))
    }

    override fun updateNumberOfPages(bookId: String, numberOfPages: Int) {
        libraryClient.updateNumberOfPages(bookId, UpdateNumberOfPages(numberOfPages))
    }

}