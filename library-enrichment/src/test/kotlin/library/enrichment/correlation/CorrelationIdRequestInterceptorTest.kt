package library.enrichment.correlation

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.willReturn
import feign.RequestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class CorrelationIdRequestInterceptorTest {

    val correlationIdHolder: CorrelationIdHolder = mock()
    val cut = CorrelationIdRequestInterceptor(correlationIdHolder)

    val template = RequestTemplate()

    @Test fun `if there is a correlation id in the holder it will be used`() {
        given { correlationIdHolder.get() } willReturn { "123-abc" }
        cut.apply(template)
        assertThat(template.headers()["X-Correlation-ID"])
                .containsOnly("123-abc")
    }

    @Test fun `if there is no correlation id in the holder one will be generated`() {
        given { correlationIdHolder.get() } willReturn { null }
        cut.apply(template)
        assertThat(template.headers()["X-Correlation-ID"])
                .allMatch { it.isNotBlank() }
                .isNotEmpty()
    }

}