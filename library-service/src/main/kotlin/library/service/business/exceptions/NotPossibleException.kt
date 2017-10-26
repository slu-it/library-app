package library.service.business.exceptions


/**
 * Base exception type used for cases where an action was assumed to be
 * possible, but was in fact not. As an example this could be transitioning
 * something to a state it is already in.
 */
open class NotPossibleException(msg: String) : RuntimeException(msg)