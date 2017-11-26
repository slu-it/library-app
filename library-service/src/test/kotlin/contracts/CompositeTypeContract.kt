package contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * This contract defines tests which must pass for any composite (multi-value)
 * type implementation.
 */
abstract class CompositeTypeContract<out T : Any> {

    /**
     * Creates a new example instance of the composite type. Must not be equal
     * to any instance produced by invoking [createOtherExampleInstances]!
     */
    abstract fun createExampleInstance(): T

    /**
     * Creates any number of example instances of the composite type. None of
     * them are allowed to be equal to the instance produced by invoking
     * [createExampleInstance]!
     */
    abstract fun createOtherExampleInstances(): List<T>

    /**
     * All of these contract's tests are grouped in one sub-set to prevent
     * accidental overrides.
     */
    @Nested inner class `is a composite type` {

        @Nested inner class `equality` {

            @Test fun `instances with different values are not considered equal`() {
                val instance = createExampleInstance()
                createOtherExampleInstances().forEach {
                    assertThat(it).isNotEqualTo(instance)
                }
            }

            @Test fun `instances with equal values are considered equal`() {
                val instance1 = createExampleInstance()
                val instance2 = createExampleInstance()
                assertThat(instance1).isEqualTo(instance2)
            }

        }

        @Nested inner class `hash codes` {

            @Test fun `instances with different values have different hash codes`() {
                val instance = createExampleInstance()
                createOtherExampleInstances().forEach {
                    assertThat(it.hashCode()).isNotEqualTo(instance.hashCode())
                }
            }

            @Test fun `instances with equal values share the same hash code`() {
                val instance1 = createExampleInstance()
                val instance2 = createExampleInstance()
                assertThat(instance1.hashCode()).isEqualTo(instance2.hashCode())
            }

        }

    }

}