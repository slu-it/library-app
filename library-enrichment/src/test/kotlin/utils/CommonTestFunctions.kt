package utils

import library.enrichment.Application
import org.testit.testutils.logrecorder.api.LogLevel
import org.testit.testutils.logrecorder.api.LogRecord
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors.toList

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

fun objectMapper() = Application().objectMapper()

fun LogRecord.warningMessages() = getEntries(LogLevel.WARN)
        .map { it.message }
        .collect(toList())

fun LogRecord.errorMessages() = getEntries(LogLevel.ERROR)
        .map { it.message }
        .collect(toList())