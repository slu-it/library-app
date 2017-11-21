package library.enrichment.core

import library.enrichment.common.logging.logger
import library.enrichment.core.events.BookAddedEvent
import org.springframework.stereotype.Component

@Component
class BookEventProcessor {

    private val log = BookEventProcessor::class.logger()

    fun bookWasAdded(event: BookAddedEvent) {
        log.info("$event")
    }

}