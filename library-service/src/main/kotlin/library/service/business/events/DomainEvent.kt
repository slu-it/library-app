package library.service.business.events

/**
 * Defines the minimum properties of a domain event.
 *
 * Any domain event class which should be dispatched using an [EventDispatcher]
 * needs to implement this interface.
 */
interface DomainEvent {

    /**
     * The type of the event. Can be used by the receiving bounded contexts to
     * differentiate between the different event types.
     */
    val type: String

    /**
     * ID to uniquely identify this event among all other events. Is formatted
     * as a UUID.
     */
    val id: String

    /** The exact time the event occurred formatted as an ISO-8601 string. */
    val timestamp: String

}