package library.service.common.correlation

import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class CorrelationIdSettingFilter(
        private val correlationIdHolder: CorrelationIdHolder
) : GenericFilterBean() {

    val correlationIdHeader = "X-Correlation-ID"

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val correlationId = getCorrelationId(request)
        try {
            correlationIdHolder.set(correlationId)
            chain.doFilter(request, response)
        } finally {
            correlationIdHolder.remove()
        }
    }

    private fun getCorrelationId(request: ServletRequest): String {
        val httpRequest = request as HttpServletRequest
        return httpRequest.getHeader(correlationIdHeader) ?: UUID.randomUUID().toString()
    }

}
