package library.service.business.events

/**
 * Interface defining all methods which need to be implemented by an event dispatcher.
 * These are used to inform other bounded contexts about [DomainEvent] occurrences.
 */
interface EventDispatcher<in T: DomainEvent> {

    /**
     * Dispatches the given [DomainEvent] to be processed by other bounded
     * contexts which might be interested in it.
     *
     * @param event the [DomainEvent] to dispatch
     */
    fun dispatch(event: T)

}