package library.enrichment.gateways.library

import feign.Headers
import feign.Param
import feign.RequestLine

@Headers("Content-Type: application/json")
interface LibraryClient {

    @RequestLine("PUT /api/books/{bookId}/authors")
    fun updateAuthors(@Param("bookId") bookId: String, payload: UpdateAuthors)

    @RequestLine("PUT /api/books/{bookId}/numberOfPages")
    fun updateNumberOfPages(@Param("bookId") bookId: String, payload: UpdateNumberOfPages)

}

data class UpdateAuthors(val authors: List<String>)
data class UpdateNumberOfPages(val numberOfPages: Int)