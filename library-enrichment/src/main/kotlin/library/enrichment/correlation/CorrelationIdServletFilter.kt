package library.enrichment.correlation

import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * [GenericFilterBean] responsible for setting the [CorrelationId] for each
 * request.
 *
 * Consumer of the library's API can provide a custom correlation ID by setting
 * the `X-Correlation-ID` header. For requests without this header a random
 * ID is generated.
 */
@Component
class CorrelationIdServletFilter(
        private val correlationId: CorrelationId
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse

        try {
            val correlationId = correlationId.setOrGenerate(request.correlationId)
            response.correlationId = correlationId

            chain.doFilter(request, response)
        } finally {
            correlationId.remove()
        }
    }

}
