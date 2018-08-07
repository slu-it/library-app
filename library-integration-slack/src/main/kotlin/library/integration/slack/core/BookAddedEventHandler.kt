package library.integration.slack.core

import org.springframework.stereotype.Component

@Component
class BookAddedEventHandler {

    fun handleBookAdded(event: BookAddedEvent) {
        //TODO: Add call to SlackService
    }
}