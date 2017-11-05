package library.enrichment.external.openlibrary

import com.fasterxml.jackson.databind.JsonNode
import feign.Param
import feign.RequestLine

interface OpenLibraryClient {

    @RequestLine("GET /api/books?bibkeys={isbn}&format=json&jscmd=data")
    fun searchBooks(@Param(value = "isbn", expander = IsbnParamExpander::class) isbn: String): JsonNode

    class IsbnParamExpander : Param.Expander {
        override fun expand(value: Any): String = "ISBN:$value"
    }

}