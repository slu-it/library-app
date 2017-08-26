package contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

abstract class ValueTypeContract<out T : Any> {

    abstract fun instanceExampleOne(): T
    abstract fun instanceExampleTwo(): T

    @Nested inner class `is a value type` {

        val exampleOfTypeOne = instanceExampleOne()
        val anotherExampleOfTypeOne = instanceExampleOne()
        val exampleOfTypeTwo = instanceExampleTwo()

        @Nested inner class `equality` {

            @Test fun `instances with different values are not considered equal`() {
                assertThat(exampleOfTypeOne).isNotEqualTo(exampleOfTypeTwo)
            }

            @Test fun `instances with equal values are considered equal`() {
                assertThat(exampleOfTypeOne).isEqualTo(anotherExampleOfTypeOne)
            }

        }

        @Nested inner class `hash codes` {

            @Test fun `instances with different values have different hash codes`() {
                assertThat(exampleOfTypeOne.hashCode()).isNotEqualTo(exampleOfTypeTwo.hashCode())
            }

            @Test fun `instances with equal values share the same hash code`() {
                assertThat(exampleOfTypeOne.hashCode()).isEqualTo(anotherExampleOfTypeOne.hashCode())
            }

        }

    }

}