package library.enrichment.correlation

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


const val CORRELATION_ID_HEADER = "X-Correlation-ID"

val HttpServletRequest.correlationId: String?
    get() = getHeader(CORRELATION_ID_HEADER)

var HttpServletResponse.correlationId: String?
    get() = getHeader(CORRELATION_ID_HEADER)
    set(value) = setHeader(CORRELATION_ID_HEADER, value)
