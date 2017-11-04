package utils

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