package library.service.business.events

/**
 * Interface defining all methods which need to be implemented by an event dispatcher.
 * These are used to inform other bounded contexts about domain events.
 */
interface EventDispatcher<in T: DomainEvent> {

    /**
     * Dispatches the given event to be processed by other bounded
     * contexts interested in it.
     *
     * @param event the domain event to dispatch
     */
    fun dispatch(event: T)

}