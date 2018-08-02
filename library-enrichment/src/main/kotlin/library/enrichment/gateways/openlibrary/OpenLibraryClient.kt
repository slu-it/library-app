package library.enrichment.gateways.openlibrary

import com.fasterxml.jackson.databind.JsonNode
import feign.Param
import feign.RequestLine

/**
 * This Feign client interface describes all relevant REST endpoints of the
 * openlibrary.org internet service. It is used to generate an actual client
 * implementation at runtime.
 *
 * For more details on Feign see: [Open Feign](https://github.com/OpenFeign/feign)
 */
interface OpenLibraryClient {

    @RequestLine("GET /api/books?bibkeys={isbn}&format=json&jscmd=data")
    fun searchBooks(@Param(value = "isbn", expander = IsbnParamExpander::class) isbn: String): JsonNode

    class IsbnParamExpander : Param.Expander {
        override fun expand(value: Any): String = "ISBN:$value"
    }

}