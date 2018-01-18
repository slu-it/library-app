package library.service.correlation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import utils.classification.UnitTest

@UnitTest
internal class CorrelationIdHolderTest {

    val cut = CorrelationIdHolder()

    @BeforeEach
    @AfterEach
    fun clearMDC() {
        MDC.remove("correlationId")
    }

    @Test fun `initially there is no correlation ID`() {
        assertThat(cut.get()).isNull()
    }

    @Test fun `correlation ID can be set`() {
        cut.set("my ID")
        assertThat(cut.get()).isEqualTo("my ID")
    }

    @Test fun `correlation ID can be removed`() {
        cut.set("my ID")
        cut.remove()
        assertThat(cut.get()).isNull()
    }

}