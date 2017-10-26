package library.service.business.exceptions

/**
 * Base exception type used for cases where values don't match their defined
 * format. This usually happens in two cases:
 *
 * 1. data provided from an external source (e.g. user input) is malformed
 * 2. data loaded from a data store was corrupted or not correctly migrated
 * to a new format
 */
open class MalformedValueException(msg: String) : RuntimeException(msg)