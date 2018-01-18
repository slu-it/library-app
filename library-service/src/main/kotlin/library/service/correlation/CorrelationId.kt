package library.service.correlation

import java.util.*

object CorrelationId {
    fun generate() = UUID.randomUUID().toString()
}