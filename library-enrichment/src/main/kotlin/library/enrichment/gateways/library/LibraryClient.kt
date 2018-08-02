package library.enrichment.gateways.library

import feign.Headers
import feign.Param
import feign.RequestLine

/**
 * This Feign client interface describes all relevant REST endpoints of the
 * main library service. It is used to generate an actual client implementation
 * at runtime.
 *
 * For more details on Feign see: [Open Feign](https://github.com/OpenFeign/feign)
 */
interface LibraryClient {

    @RequestLine("GET /api")
    fun ping()

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /api/books/{bookId}/authors")
    fun updateAuthors(@Param("bookId") bookId: String, payload: UpdateAuthors)

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /api/books/{bookId}/numberOfPages")
    fun updateNumberOfPages(@Param("bookId") bookId: String, payload: UpdateNumberOfPages)

}

data class UpdateAuthors(val authors: List<String>)
data class UpdateNumberOfPages(val numberOfPages: Int)