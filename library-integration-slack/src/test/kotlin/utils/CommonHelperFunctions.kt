package utils

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Returns an objectMapper instance for test purposes.
 */
fun objectMapper() = ObjectMapper().apply {
    findAndRegisterModules()
}
