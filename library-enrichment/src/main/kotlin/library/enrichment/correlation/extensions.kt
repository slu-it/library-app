package library.enrichment.correlation

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


internal const val CORRELATION_ID_HEADER = "X-Correlation-ID"

internal val HttpServletRequest.correlationId: String?
    get() = getHeader(CORRELATION_ID_HEADER)

internal var HttpServletResponse.correlationId: String?
    get() = getHeader(CORRELATION_ID_HEADER)
    set(value) = setHeader(CORRELATION_ID_HEADER, value)
