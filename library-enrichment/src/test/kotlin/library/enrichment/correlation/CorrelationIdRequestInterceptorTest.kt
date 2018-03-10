package library.enrichment.correlation

import feign.RequestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class CorrelationIdRequestInterceptorTest {

    val correlationId = CorrelationId()
    val cut = CorrelationIdRequestInterceptor(correlationId)

    val template = RequestTemplate()

    @Test fun `if there is a correlation id in the holder it will be used`() {
        correlationId.setOrGenerate("123-abc")
        cut.apply(template)
        assertThat(template.headers()["X-Correlation-ID"]).containsOnly("123-abc")
    }

    @Test fun `if there is no correlation id in the holder one will be generated`() {
        correlationId.remove()
        cut.apply(template)
        assertThat(template.headers()["X-Correlation-ID"])
                .allMatch { it.isNotBlank() }
                .isNotEmpty()
    }

}