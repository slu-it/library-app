package library.service.correlation

import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.classification.UnitTest
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@UnitTest
internal class CorrelationIdServletFilterTest {

    val correlationIdHolder: CorrelationIdHolder = mockk(relaxed = true)
    val cut = CorrelationIdServletFilter(correlationIdHolder)

    val request: HttpServletRequest = mockk(relaxed = true)
    val response: HttpServletResponse = mockk(relaxed = true)
    val filterChain: FilterChain = mockk(relaxed = true)

    @Test fun `correlation ID is taken from request and removed when request was processed`() {
        every { request.getHeader("X-Correlation-ID") } returns "abc-123"

        cut.doFilter(request, response, filterChain)

        verifyOrder {
            correlationIdHolder.set("abc-123")
            filterChain.doFilter(request, response)
            correlationIdHolder.remove()
        }
    }

    @Test fun `if no correlation ID is provided one is generated`() {
        val idSlot = slot<String>()
        every { request.getHeader("X-Correlation-ID") } returns null

        cut.doFilter(request, response, filterChain)

        verifyOrder {
            correlationIdHolder.set(capture(idSlot))
            filterChain.doFilter(request, response)
            correlationIdHolder.remove()
        }
        with(idSlot.captured) {
            assertThat(this).isNotBlank()
            assertThat(UUID.fromString(this)).isNotNull()
        }
    }

    @Test fun `custom correlation ID header is set on response`() {
        every { request.getHeader("X-Correlation-ID") } returns "abc-123"

        cut.doFilter(request, response, filterChain)

        verifyOrder {
            response.setHeader("X-Correlation-ID", "abc-123")
            filterChain.doFilter(request, response)
        }
    }

    @Test fun `generated correlation ID header is set on response`() {
        val headerSlot = slot<String>()
        every { request.getHeader("X-Correlation-ID") } returns null

        cut.doFilter(request, response, filterChain)

        verifyOrder {
            response.setHeader("X-Correlation-ID", capture(headerSlot))
            filterChain.doFilter(request, response)
        }
        with(headerSlot.captured) {
            assertThat(this).isNotBlank()
            assertThat(UUID.fromString(this)).isNotNull()
        }
    }

    @Test fun `correlation ID removed, even is case of an exception`() {
        every { filterChain.doFilter(any(), any()) } throws RuntimeException()

        assertThrows<RuntimeException> {
            cut.doFilter(request, response, filterChain)
        }

        verify { correlationIdHolder.remove() }
    }

}