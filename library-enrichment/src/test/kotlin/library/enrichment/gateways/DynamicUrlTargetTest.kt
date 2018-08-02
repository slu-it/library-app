package library.enrichment.gateways

import feign.RequestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class DynamicUrlTargetTest {

    var url = "http://example.com"
    val cut = DynamicUrlTarget("test-client", TestClient::class) { url }

    @Nested inner class `properties are returned correctly` {

        @Test fun name() {
            assertThat(cut.name()).isEqualTo("test-client")
        }

        @Test fun url() {
            assertThat(cut.url()).isEqualTo("http://example.com")
        }

        @Test fun type() {
            assertThat(cut.type()).isEqualTo(TestClient::class.java)
        }

    }

    @Test fun `url is resolved for each call`() {
        url = "http://some-url.com"
        assertThat(cut.url()).isEqualTo("http://some-url.com")
        url = "https://some-other-url.com"
        assertThat(cut.url()).isEqualTo("https://some-other-url.com")
    }

    @Test fun `requests are modified with correct base url`() {
        val requestTemplate = RequestTemplate().apply {
            method("GET")
            append("/some/endpoint")
            query("param1", "value")
        }
        val request = cut.apply(requestTemplate)
        assertThat(request.url()).isEqualTo("http://example.com/some/endpoint?param1=value")
    }

    interface TestClient

}