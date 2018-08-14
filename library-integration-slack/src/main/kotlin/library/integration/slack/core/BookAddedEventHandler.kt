package library.integration.slack.core

import org.springframework.stereotype.Component

@Component
class BookAddedEventHandler(
        private val slack: Slack
) {
    fun handleBookAdded(event: BookAddedEvent) {
        val bookAddedText = "The book '${event.bookId}'was just added to the library."
        slack.postMessage(bookAddedText)
    }
}