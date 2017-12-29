package library.service.api.correlation

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@UnitTest
internal class CorrelationIdSettingFilterTest {

    val correlationIdHolder: CorrelationIdHolder = mock()
    val cut = CorrelationIdSettingFilter(correlationIdHolder)

    val request: HttpServletRequest = mock()
    val response: HttpServletResponse = mock()
    val filterChain: FilterChain = mock()

    @Test fun `correlation ID is taken from request and removed when request was processed`() {
        given { request.getHeader("X-Correlation-ID") }.willReturn("abc-123")

        cut.doFilter(request, response, filterChain)

        with(inOrder(correlationIdHolder, filterChain)) {
            verify(correlationIdHolder).set("abc-123")
            verify(filterChain).doFilter(request, response)
            verify(correlationIdHolder).remove()
        }
    }

    @Test fun `if no correlation ID is provided one is generated`() {
        given { request.getHeader("X-Correlation-ID") }.willReturn(null)

        cut.doFilter(request, response, filterChain)

        with(inOrder(correlationIdHolder, filterChain)) {
            verify(correlationIdHolder).set(check {
                assertThat(it).isNotBlank()
                assertThat(UUID.fromString(it)).isNotNull()
            })
            verify(filterChain).doFilter(request, response)
            verify(correlationIdHolder).remove()
        }
    }

    @Test fun `custom correlation ID header is set on response`() {
        given { request.getHeader("X-Correlation-ID") }.willReturn("abc-123")

        cut.doFilter(request, response, filterChain)

        with(inOrder(response, filterChain)) {
            verify(response).setHeader("X-Correlation-ID", "abc-123")
            verify(filterChain).doFilter(request, response)
        }
    }

    @Test fun `generated correlation ID header is set on response`() {
        given { request.getHeader("X-Correlation-ID") }.willReturn(null)

        cut.doFilter(request, response, filterChain)

        with(inOrder(response, filterChain)) {
            verify(response).setHeader(eq("X-Correlation-ID"), check {
                assertThat(it).isNotBlank()
                assertThat(UUID.fromString(it)).isNotNull()
            })
            verify(filterChain).doFilter(request, response)
        }
    }

    @Test fun `correlation ID removed, even is case of an exception`() {
        given { filterChain.doFilter(any(), any()) }.willThrow(RuntimeException::class.java)

        assertThrows(RuntimeException::class.java, {
            cut.doFilter(request, response, filterChain)
        })

        verify(correlationIdHolder).remove()
    }

}