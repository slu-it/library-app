package library.enrichment.correlation

import java.util.*


object CorrelationId {
    fun generate() = UUID.randomUUID().toString()
}