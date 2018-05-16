package library.enrichment.gateways.library

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willThrow
import feign.FeignException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Status
import utils.classification.UnitTest


@UnitTest
internal class LibraryHealthIndicatorTest {

    val libraryClient: LibraryClient = mock()
    val cut = LibraryHealthIndicator(libraryClient)

    @Test fun `status is UP if ping was successful`() {
        with(cut.health()) {
            assertThat(status).isEqualTo(Status.UP)
            assertThat(details).isEmpty()
        }
        verify(libraryClient).ping()
    }

    @Test fun `status is DOWN if ping failed for any reason`() {
        given { libraryClient.ping() } willThrow { SomeFeignException() }
        with(cut.health()) {
            assertThat(status).isEqualTo(Status.DOWN)
            assertThat(details["status"]).isEqualTo(401)
            assertThat(details["error"]).isEqualTo("${SomeFeignException::class.java.name}: message")
        }
    }

    internal class SomeFeignException : FeignException(401, "message")

}