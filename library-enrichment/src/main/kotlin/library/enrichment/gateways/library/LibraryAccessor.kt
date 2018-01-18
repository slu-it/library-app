package library.enrichment.gateways.library

import library.enrichment.core.BookData
import library.enrichment.core.Library
import org.springframework.stereotype.Component

@Component
class LibraryAccessor(
        private val libraryClient: LibraryClient
) : Library {

    override fun updateBookData(bookId: String, updateData: BookData) {

        val authors = updateData.authors
        if (authors.isNotEmpty()) {
            libraryClient.updateAuthors(bookId, UpdateAuthors(authors))
        }

        val numberOfPages = updateData.numberOfPages
        if (numberOfPages != null) {
            libraryClient.updateNumberOfPages(bookId, UpdateNumberOfPages(numberOfPages))
        }

    }

}