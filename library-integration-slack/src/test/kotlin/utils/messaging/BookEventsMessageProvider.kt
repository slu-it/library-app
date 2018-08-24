package utils.messaging

import library.integration.slack.core.BookAddedEvent

class BookEventsMessageProvider {
    companion object {
        val bookAddedEvent = BookAddedEvent(
                isbn = "9780132350884",
                title = "Clean Code: A Handbook of Agile Software Craftsmanship"
        )
    }
}