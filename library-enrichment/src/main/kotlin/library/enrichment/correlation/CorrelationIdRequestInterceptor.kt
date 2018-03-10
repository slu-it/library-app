package library.enrichment.correlation

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Component

/**
 * This Feign [RequestInterceptor] will set the `X-Correlation-ID` header for
 * each send request. This header can be used to trace processing of a single
 * request over many services.
 *
 * If there is a correlation ID present for the current thread, it will be used.
 * Otherwise a new ID is generated.
 *
 * @see CorrelationId
 * @see CorrelationId
 */
@Component
class CorrelationIdRequestInterceptor(
        private val correlationId: CorrelationId
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        template.header(CORRELATION_ID_HEADER, correlationId.get())
    }

}