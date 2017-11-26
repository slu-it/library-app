package library.enrichment.library

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class UpdateBookPayload(
        val authors: List<String>,
        val numberOfPages: Int?
)