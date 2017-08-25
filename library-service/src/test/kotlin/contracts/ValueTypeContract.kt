package contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

abstract class ValueTypeContract<out T : Any> {

    abstract fun newExampleInstanceOne(): T
    abstract fun newExampleInstanceTwo(): T

    @Nested inner class `is a value type` {

        @Nested inner class `equality` {

            @Test fun `instances with different values are not equal`() {
                val instanceOne = newExampleInstanceOne()
                val instanceTwo = newExampleInstanceTwo()

                assertThat(instanceOne).isNotEqualTo(instanceTwo)
                assertThat(instanceOne.hashCode()).isNotEqualTo(instanceTwo.hashCode())
            }

            @Test fun `instances with equal values are equal`() {
                val instanceOne = newExampleInstanceOne()
                val instanceTwo = newExampleInstanceOne()

                assertThat(instanceOne).isEqualTo(instanceTwo)
                assertThat(instanceOne.hashCode()).isEqualTo(instanceTwo.hashCode())
            }

        }

        @Nested inner class `hash codes` {

            @Test fun `instances with different values have different hash codes`() {
                val instanceOne = newExampleInstanceOne()
                val instanceTwo = newExampleInstanceTwo()
                assertThat(instanceOne.hashCode()).isNotEqualTo(instanceTwo.hashCode())
            }

            @Test fun `instances with equal values share the same hash code`() {
                val instanceOne = newExampleInstanceOne()
                val instanceTwo = newExampleInstanceOne()
                assertThat(instanceOne.hashCode()).isEqualTo(instanceTwo.hashCode())
            }

        }

    }

}