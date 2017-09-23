package library.service.dispatcher

import library.service.business.books.BookEventDispatcher
import library.service.business.books.domain.events.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DummyBookEventDispatcher : BookEventDispatcher {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun dispatch(event: BookEvent) = when (event) {
        is BookAdded -> log(event)
        is BookRemoved -> log(event)
        is BookBorrowed -> log(event)
        is BookReturned -> log(event)
    }

    private fun log(event: BookEvent) = log.info("Event occurred: $event")

}
