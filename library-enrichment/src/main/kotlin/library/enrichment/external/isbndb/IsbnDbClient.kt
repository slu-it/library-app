package library.enrichment.external.isbndb

import com.fasterxml.jackson.databind.JsonNode
import feign.Param
import feign.RequestLine

interface IsbnDbClient {

    @RequestLine("GET /api/v2/json/{apiKey}/book/{isbn}")
    fun searchBooks(@Param("apiKey") apiKey: String, @Param("isbn") isbn: String): JsonNode?

}