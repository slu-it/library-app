package library.service.logging

import org.springframework.stereotype.Component
import org.springframework.web.filter.CommonsRequestLoggingFilter

/**
 * HTTP servlet filter responsible for logging request entry and exit into
 * the application. The logged information includes:
 *
 * - client info like IP address and used browser
 * - request and response headers
 * - query strings
 */
@Component
class RequestLoggingFilter : CommonsRequestLoggingFilter() {

    init {
        isIncludeClientInfo = true
        isIncludeHeaders = true
        isIncludeQueryString = true
    }

}
