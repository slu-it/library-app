package utils.extensions

import org.testit.testutils.logrecorder.api.LogRecord

fun LogRecord.firstEntry() = this.entries.findFirst().orElseThrow { IllegalStateException("no log entries") }