package contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

interface ValueTypeContract<out T : Any> {

    fun newExampleInstanceOne(): T
    fun newExampleInstanceTwo(): T

    @Test fun `(value type) two instances with distinct values are considered not equal`() {
        val instanceOne = newExampleInstanceOne()
        val instanceTwo = newExampleInstanceTwo()

        assertThat(instanceOne).isNotEqualTo(instanceTwo)
        assertThat(instanceOne.hashCode()).isNotEqualTo(instanceTwo.hashCode())
    }

    @Test fun `(value type) two instances with equal values are considered equal`() {
        val instanceOne = newExampleInstanceOne()
        val instanceTwo = newExampleInstanceOne()

        assertThat(instanceOne).isEqualTo(instanceTwo)
        assertThat(instanceOne.hashCode()).isEqualTo(instanceTwo.hashCode())
    }

}