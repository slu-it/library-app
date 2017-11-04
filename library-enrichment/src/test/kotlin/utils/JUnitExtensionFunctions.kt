package utils

import org.junit.jupiter.api.Assertions
import kotlin.reflect.KClass


fun <T : Throwable> assertThrows(expectedType: KClass<T>, executable: () -> Unit): T {
    return Assertions.assertThrows(expectedType.java, executable)
}