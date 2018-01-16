package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class UpdateAuthorsRequestTest {

    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "authors": ["Foo", "Bar"] } """
        val cut = objectMapper.readValue(json, UpdateAuthorsRequest::class.java)
        assertThat(cut.authors).containsExactly("Foo", "Bar")
    }

    @Nested inner class `bean validation for 'authors'` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Test fun `null is not allowed`() {
            val cut = UpdateAuthorsRequest(null)
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must not be empty")
        }

        @Test fun `empty is not allowed`() {
            val cut = UpdateAuthorsRequest(emptyList())
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("must not be empty")
        }

    }

}