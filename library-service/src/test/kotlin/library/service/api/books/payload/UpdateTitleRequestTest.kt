package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class UpdateTitleRequestTest {

    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "title": "Hello World" } """
        val cut = objectMapper.readValue(json, UpdateTitleRequest::class.java)
        assertThat(cut.title).isEqualTo("Hello World")
    }

    @Nested inner class `bean validation for 'title'` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Test fun `null is not allowed`() {
            val cut = UpdateTitleRequest(null)
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must not be blank")
        }

        @Test fun `empty is not allowed`() {
            val cut = UpdateTitleRequest("")
            val result = validator.validate(cut).toList()
            assertThat(result.map { it.message }).containsOnly(
                    "size must be between 1 and 256",
                    "must not be blank"
            )
        }

        @Test fun `blank is not allowed`() {
            val cut = UpdateTitleRequest(" ")
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must not be blank")
        }

        @Test fun `values between 1 and 256 characters are valid`() {
            (1..256)
                    .map { "".padEnd(it, 'a') }
                    .map { UpdateTitleRequest(it) }
                    .map { validator.validate(it).toList() }
                    .forEach { assertThat(it).isEmpty() }
        }

        @Test fun `values with more than 256 characters are invalid`() {
            val value = "".padEnd(257, 'a')
            val cut = UpdateTitleRequest(value)
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("size must be between 1 and 256")
        }

    }

}