package library.service.api

import library.service.business.exceptions.MalformedValueException
import library.service.business.exceptions.NotFoundException
import library.service.business.exceptions.NotPossibleException
import library.service.correlation.CorrelationIdHolder
import library.service.logging.logger
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Clock
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

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

    private val log = ErrorHandlers::class.logger

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handle(e: NotFoundException): ErrorDescription {
        log.debug("received request for non existing resource:", e)
        return errorDescription(
                httpStatus = HttpStatus.NOT_FOUND,
                message = e.message!!
        )
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(NotPossibleException::class)
    fun handle(e: NotPossibleException): ErrorDescription {
        log.debug("received conflicting request:", e)
        return errorDescription(
                httpStatus = HttpStatus.CONFLICT,
                message = e.message!!
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedValueException::class)
    fun handle(e: MalformedValueException): ErrorDescription {
        log.debug("received malformed request:", e)
        return errorDescription(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = e.message!!
        )
    }

    /** In case the request parameter has wrong type. */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handle(e: MethodArgumentTypeMismatchException): ErrorDescription {
        log.debug("received bad request:", e)
        return errorDescription(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = "The request's '${e.name}' parameter is malformed."
        )
    }

    /** In case the request body is malformed or non existing. */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ErrorDescription {
        log.debug("received bad request:", e)
        return errorDescription(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = "The request's body could not be read. It is either empty or malformed."
        )
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

        return errorDescription(
                httpStatus = HttpStatus.BAD_REQUEST,
                message = "The request's body is invalid. See details...",
                details = sortedDetails
        )
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException::class)
    fun handle(e: AccessDeniedException, request: HttpServletRequest): ErrorDescription {
        val userName = SecurityContextHolder.getContext()?.authentication?.name
        log.debug("blocked illegal access from user [{}]: {} {}", userName, request.method, request.requestURI)
        return errorDescription(
                httpStatus = HttpStatus.FORBIDDEN,
                message = "You don't have the necessary rights to to this."
        )
    }

    /** In case any other exception occurs. */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handle(e: Exception): ErrorDescription {
        log.error("internal server error occurred:", e)
        return errorDescription(
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
                message = "An internal server error occurred, see server logs for more information."
        )
    }

    private fun errorDescription(
            httpStatus: HttpStatus,
            message: String,
            details: List<String> = emptyList()
    ) = ErrorDescription(
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            timestamp = OffsetDateTime.now(clock).toString(),
            correlationId = correlationIdHolder.get(),
            message = message,
            details = details
    )

}