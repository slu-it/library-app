package library.service.api

import library.service.business.exceptions.MalformedValueException
import library.service.business.exceptions.NotFoundException
import library.service.business.exceptions.NotPossibleException
import library.service.common.correlation.CorrelationIdHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Clock
import java.time.OffsetDateTime

/**
 * Defines a number of commonly used exception handlers for REST endpoints.
 *
 * This includes basic handlers for common business exceptions like:
 * - [NotFoundException]
 * - [NotPossibleException]
 * - [MalformedValueException]
 *
 * As well as a number of framework exceptions related to bad user input.
 *
 * This class should _not_ contain any domain specific exception handlers.
 * Those need to be defined in the corresponding controller!
 */
@RestControllerAdvice
class ErrorHandlers(
        private val clock: Clock,
        private val correlationIdHolder: CorrelationIdHolder
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handle(e: NotFoundException): ErrorDescription {
        log.debug("received request for non existing resource:", e)
        return errorDescription(e.message!!)
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NotPossibleException::class)
    fun handle(e: NotPossibleException): ErrorDescription {
        log.debug("received conflicting request:", e)
        return errorDescription(e.message!!)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedValueException::class)
    fun handle(e: MalformedValueException): ErrorDescription {
        log.debug("received malformed request:", e)
        return errorDescription(e.message!!)
    }

    /** In case the request parameter has wrong type. */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handle(e: MethodArgumentTypeMismatchException): ErrorDescription {
        log.debug("received bad request:", e)
        return errorDescription("The request's '${e.name}' parameter is malformed.")
    }

    /** In case the request body is malformed or non existing. */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ErrorDescription {
        log.debug("received bad request:", e)
        return errorDescription("The request's body could not be read. It is either empty or malformed.")
    }

    /** In case a validation on a request body property fails */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ErrorDescription {
        log.debug("received bad request:", e)

        val fieldDetails = e.bindingResult.fieldErrors.map { "The field '${it.field}' ${it.defaultMessage}." }
        val globalDetails = e.bindingResult.globalErrors.map { it.defaultMessage }
        val details = fieldDetails + globalDetails
        val sortedDetails = details.sorted()

        return errorDescription("The request's body is invalid. See details...", sortedDetails)
    }

    private fun errorDescription(description: String, details: List<String> = emptyList()): ErrorDescription {
        val timestamp = OffsetDateTime.now(clock).toString()
        val correlationId = correlationIdHolder.get()
        return ErrorDescription(
                timestamp = timestamp,
                correlationId = correlationId,
                description = description,
                details = details
        )
    }

}