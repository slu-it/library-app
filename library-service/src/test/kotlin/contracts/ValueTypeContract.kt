package contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * This contract defines tests which must pass for any value type implementation.
 */
abstract class ValueTypeContract<out T : Any, V : Any> {

    /**
     * Returns an example value to be used to create instances of the value
     * type for testing. Must not be equal to the value produced by invoking
     * [getAnotherValueExample].
     */
    abstract fun getValueExample(): V

    /**
     * Returns an example value to be used to create instances of the value
     * type for testing. Must not be equal to the value produced by invoking
     * [getValueExample].
     */
    abstract fun getAnotherValueExample(): V

    /**
     * Creates a new instance of the value type for the given `value` parameter.
     * These instances are used by the contract tests to verify certain
     * characteristics.
     */
    abstract fun createNewInstance(value: V): T

    /**
     * All of these contract's tests are grouped in one sub-set to prevent
     * accidental overrides.
     */
    @Nested inner class `is a value type` {

        val example = getValueExample()
        val anotherExample = getAnotherValueExample()

        @Nested inner class `equality` {

            @Test fun `instances with different values are not considered equal`() {
                val instance1 = createNewInstance(example)
                val instance2 = createNewInstance(anotherExample)
                assertThat(instance1).isNotEqualTo(instance2)
            }

            @Test fun `instances with equal values are considered equal`() {
                val instance1 = createNewInstance(example)
                val instance2 = createNewInstance(example)
                assertThat(instance1).isEqualTo(instance2)
            }

        }

        @Nested inner class `hash codes` {

            @Test fun `instances with different values have different hash codes`() {
                val instance1 = createNewInstance(example)
                val instance2 = createNewInstance(anotherExample)
                assertThat(instance1.hashCode()).isNotEqualTo(instance2.hashCode())
            }

            @Test fun `instances with equal values share the same hash code`() {
                val instance1 = createNewInstance(example)
                val instance2 = createNewInstance(example)
                assertThat(instance1.hashCode()).isEqualTo(instance2.hashCode())
            }

        }

    }

}