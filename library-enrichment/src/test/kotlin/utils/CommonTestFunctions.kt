package utils

import library.enrichment.Application
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Returns a [Clock] with a fixed value of the given ISO timestamp.
 *
 * Example: `2017-10-30T12:34:56.789Z`.
 */
fun clockWithFixedTime(isoTimestamp: String): Clock {
    val timestamp = ZonedDateTime.parse(isoTimestamp)
    return Clock.fixed(timestamp.toInstant(), ZoneId.from(timestamp))
}

/**
 * Reads the file corresponding to the given path from the classpath
 * as a String. An UTF-8 charset is assumed.
 */
fun readFile(filePath: String): String {
    val classLoader = Application::class.java.classLoader
    val resource = classLoader.getResource(filePath) ?: error("File not found: $filePath")
    BufferedReader(InputStreamReader(resource.openStream(), Charsets.UTF_8)).use {
        return it.readText()
    }
}
