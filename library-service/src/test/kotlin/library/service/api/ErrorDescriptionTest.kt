package library.service.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import java.util.*

@UnitTest
internal class ErrorDescriptionTest {

    val objectMapper = ObjectMapper()

    @Test fun `can be serialized to JSON`() {
        val cut = ErrorDescription(
                status = 400,
                error = "Bad Request",
                timestamp = "2017-08-20T12:34:56.789Z",
                correlationId = UUID.randomUUID().toString(),
                message = "message",
                details = listOf(
                        "detail #1",
                        "detail #2"
                )
        )
        val json = objectMapper.writeValueAsString(cut)
        assertThat(json).isNotBlank()
    }

}