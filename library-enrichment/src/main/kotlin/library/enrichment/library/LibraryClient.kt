package library.enrichment.library

import feign.Param
import feign.RequestLine
import feign.Response

interface LibraryClient {

    @RequestLine("POST /api/books/{bookId}/_update")
    fun updateBook(@Param("bookId") bookId: String, payload: UpdateBookPayload): Response

}