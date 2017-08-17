package library.service.common.logging

import org.springframework.stereotype.Component
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Component
class RequestLoggingFilter : CommonsRequestLoggingFilter() {

    init {
        isIncludeClientInfo = true
        isIncludeHeaders = true
        isIncludeQueryString = true
    }

}
