package library.enrichment.correlation

import java.util.*


internal object CorrelationId {
    fun generate() = UUID.randomUUID().toString()
}