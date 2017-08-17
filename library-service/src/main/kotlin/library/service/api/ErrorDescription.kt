package library.service.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY

@JsonInclude(NON_EMPTY)
data class ErrorDescription(
        val timestamp: String,
        val correlationId: String?,
        val description: String,
        val details: List<String> = emptyList()
)