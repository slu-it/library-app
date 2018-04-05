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
                val cut = UpdateNumberOfPagesRequest(it)
                assertThat(validate(cut)).isEmpty()
            }
        }

        @ValueSource(ints = [1, 10, 100, 1_000, 10_000, Int.MAX_VALUE])
        @ParameterizedTest fun `valid value examples`(numberOfPages: Int) {
            val cut = UpdateNumberOfPagesRequest(numberOfPages)
            assertThat(validate(cut)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val nullError = "must not be null"
            private val minValueError = "must be greater than or equal to 1"

            @Test fun `null`() {
                val cut = UpdateNumberOfPagesRequest(null)
                assertThat(validate(cut)).containsOnly(nullError)
            }

            @Test fun `zero pages`() {
                val cut = UpdateNumberOfPagesRequest(0)
                assertThat(validate(cut)).containsOnly(minValueError)
            }

            @Test fun `negative numbers`() {
                val cut = UpdateNumberOfPagesRequest(-1)
                assertThat(validate(cut)).containsOnly(minValueError)
            }

        }

    }

}