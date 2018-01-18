package library.enrichment.correlation

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Component

@Component
class CorrelationIdRequestInterceptor(
        private val correlationIdHolder: CorrelationIdHolder
) : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        template.header(CORRELATION_ID_HEADER, getCorrelationId())
    }

    private fun getCorrelationId() = correlationIdHolder.get() ?: CorrelationId.generate()

}