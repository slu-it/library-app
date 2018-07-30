package utils

import library.enrichment.Application
import org.springframework.core.io.ClassPathResource
import org.testit.testutils.logrecorder.api.LogLevel
import org.testit.testutils.logrecorder.api.LogRecord
import java.util.stream.Collectors.toList

/**
 * Reads the file corresponding to the given path from the classpath
 * as a String. An UTF-8 charset is assumed.
 */
fun readFile(filePath: String): String = ClassPathResource(filePath).file.readText(Charsets.UTF_8)

fun objectMapper() = Application().objectMapper()

fun LogRecord.warningMessages() = getEntries(LogLevel.WARN)
        .map { it.message }
        .collect(toList())

fun LogRecord.errorMessages() = getEntries(LogLevel.ERROR)
        .map { it.message }
        .collect(toList())