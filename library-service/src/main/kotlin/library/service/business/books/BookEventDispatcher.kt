package library.service.business.books

import library.service.business.books.domain.events.BookEvent

/**
 * Interface defining all methods which need to be implemented by an event dispatcher
 * in order to handle the domain events of books.
 */
interface BookEventDispatcher {

    /**
     * Dispatches the given [BookEvent] to be processed by other bounded
     * contexts interested in it.
     *
     * @param event the book domain event to dispatch
     */
    fun dispatch(event: BookEvent)

}