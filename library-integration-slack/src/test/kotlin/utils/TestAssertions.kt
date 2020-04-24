package utils

import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.assertj.core.api.ObjectAssert

/** Generic assertions for AssertJ */

infix fun <T> T.shouldBeEqualTo(expected: T): ObjectAssert<T> = Assertions
    .assertThat(this)
    .isEqualTo(expected)

infix fun <T> List<T>.shouldContainOnly(expected: T): ListAssert<T> = Assertions
    .assertThat(this).containsOnly(expected)