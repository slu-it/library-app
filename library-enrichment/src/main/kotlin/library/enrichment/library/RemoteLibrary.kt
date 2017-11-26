package library.enrichment.library

import library.enrichment.core.BookData
import library.enrichment.core.Library
import org.springframework.stereotype.Component

@Component
class RemoteLibrary(
        private val libraryClient: LibraryClient
) : Library {

    override fun updateBookData(bookId: String, updateData: BookData) {
        val payload = UpdateBookPayload(
                authors = updateData.authors,
                numberOfPages = updateData.numberOfPages
        )
        libraryClient.updateBook(bookId, payload)
    }

}