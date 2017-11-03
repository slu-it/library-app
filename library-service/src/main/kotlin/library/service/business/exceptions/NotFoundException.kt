package library.service.business.exceptions

/**
 * Base exception type used for cases where something that was assumed to exist,
 * in fact didn't. As an example this could be an required data set from a data
 * store.
 */
open class NotFoundException(msg: String) : RuntimeException(msg)