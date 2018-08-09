package library.integration.slack.core

import library.integration.slack.services.slack.SlackMessageAccessor
import org.springframework.stereotype.Component

@Component
class BookAddedEventHandler(
        private val slackMessageAccessor: SlackMessageAccessor
) {
    fun handleBookAdded(event: BookAddedEvent) {
        val bookAddedText = "The book '${event.bookId}'was just added to the library."
        slackMessageAccessor.postMessage(bookAddedText)
    }
}