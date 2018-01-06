package library.service.logging

import org.springframework.stereotype.Component
import org.springframework.web.filter.AbstractRequestLoggingFilter
import javax.annotation.PostConstruct
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

/**
 * Servlet [Filter] responsible for logging all request to this service's HTTP
 * endpoints.
 *
 * This filter is only active if the log level for this class is set to `DEBUG`
 * or lower. Log entries are created on the `DEBUG` level.
 *
 * The logged information includes:
 * - client info like IP address and used browser
 * - request and response headers
 * - query strings
 *
 * @see AbstractRequestLoggingFilter
 */
@Component
class RequestLoggingFilter : AbstractRequestLoggingFilter() {

    private val log = RequestLoggingFilter::class.logger

    @PostConstruct
    fun init() {
        isIncludeClientInfo = true
        isIncludeQueryString = true
        isIncludeHeaders = true
        isIncludePayload = false

        setBeforeMessagePrefix("Received Request [")
        setBeforeMessageSuffix("]")
        setAfterMessagePrefix("Processed Request [")
        setAfterMessageSuffix("]")
    }

    override fun shouldLog(request: HttpServletRequest) = log.isDebugEnabled
    override fun beforeRequest(request: HttpServletRequest, message: String) = log.debug(message)
    override fun afterRequest(request: HttpServletRequest, message: String) = log.debug(message)

}
