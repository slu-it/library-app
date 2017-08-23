package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class BorrowBookRequestBodyTest {

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "borrower": "Someone"} """
        val cut = ObjectMapper().readValue(json, BorrowBookRequestBody::class.java)
        assertThat(cut.borrower).isEqualTo("Someone")
    }

    @Nested inner class `bean validation for 'borrower'` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Test fun `null is not allowed`() {
            val cut = BorrowBookRequestBody().apply { borrower = null }
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("may not be empty")
        }

        @Test fun `empty is not allowed`() {
            val cut = BorrowBookRequestBody().apply { borrower = "" }
            val result = validator.validate(cut).toList()
            assertThat(result.map { it.message }).containsOnly(
                    "size must be between 1 and 50",
                    "may not be empty"
            )
        }

        @Test fun `blank is not allowed`() {
            val cut = BorrowBookRequestBody().apply { borrower = " " }
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("may not be empty")
        }

        @Test fun `values between 1 and 50 characters are valid`() {
            (1..50)
                    .map { "".padEnd(it, 'a') }
                    .map { BorrowBookRequestBody().apply { borrower = it } }
                    .map { validator.validate(it).toList() }
                    .forEach { assertThat(it).isEmpty() }
        }

        @Test fun `values with more than 50 characters are invalid`() {
            val value = "".padEnd(51, 'a')
            val cut = BorrowBookRequestBody().apply { borrower = value }
            val result = validator.validate(cut).toList()
            assertThat(result[0].message).isEqualTo("size must be between 1 and 50")
        }

    }

}