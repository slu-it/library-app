package library.service.api.books.payload

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.UnitTest
import java.util.*

@UnitTest
internal class UpdateNumberOfPagesRequestTest : AbstractPayloadTest<UpdateNumberOfPagesRequest>() {

    override val payloadType = UpdateNumberOfPagesRequest::class

    override val jsonExample = """ { "numberOfPages": 128 } """
    override val deserializedExample = UpdateNumberOfPagesRequest(128)

    private val random = Random()

    @Nested inner class `numberOfPages property validation` {

        @Test fun `any values between 1 and MAX int are valid`() {
            val randomValues = (1..100).map { random.nextInt(Int.MAX_VALUE) + 1 }
            randomValues.forEach {
                assertThat(validate(it)).isEmpty()
            }
        }

        @ValueSource(ints = [1, 10, 100, 1_000, 10_000, Int.MAX_VALUE])
        @ParameterizedTest fun `valid value examples`(numberOfPages: Int) {
            assertThat(validate(numberOfPages)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val nullError = "must not be null"
            private val minValueError = "must be greater than or equal to 1"

            @Test fun `null`() {
                assertThat(validate(null)).containsOnly(nullError)
            }

            @Test fun `zero pages`() {
                assertThat(validate(0)).containsOnly(minValueError)
            }

            @Test fun `negative numbers`() {
                assertThat(validate(-1)).containsOnly(minValueError)
            }

        }

        private fun validate(numberOfPages: Int?) = validate(UpdateNumberOfPagesRequest(numberOfPages))

    }

}