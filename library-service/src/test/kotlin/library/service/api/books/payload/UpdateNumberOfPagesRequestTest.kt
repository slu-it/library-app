package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class UpdateNumberOfPagesRequestTest {

    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "numberOfPages": 128 } """
        val cut = objectMapper.readValue(json, UpdateNumberOfPagesRequest::class.java)
        assertThat(cut.numberOfPages).isEqualTo(128)
    }

    @Nested inner class `bean validation for 'numberOfPages'` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Test fun `null is not allowed`() {
            val cut = UpdateNumberOfPagesRequest(null)
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must not be null")
        }

        @Test fun `less than one page is not allowed`() {
            val cut = UpdateNumberOfPagesRequest(0)
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must be greater than or equal to 1")
        }

        @Test fun `one page is allowed`() {
            val cut = UpdateNumberOfPagesRequest(1)
            val result = validator.validate(cut).toList()
            assertThat(result).isEmpty()
        }

    }

}