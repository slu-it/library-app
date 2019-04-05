package library.service.api

import io.mockk.every
import io.mockk.mockk
import library.service.business.exceptions.MalformedValueException
import library.service.business.exceptions.NotFoundException
import library.service.business.exceptions.NotPossibleException
import library.service.correlation.CorrelationIdHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import utils.MutableClock
import utils.classification.IntegrationTest
import utils.testapi.TestController
import utils.testapi.TestService
import java.util.*


@IntegrationTest
@WebMvcTest(TestController::class, secure = false)
@ComponentScan("utils.testapi")
internal class ErrorHandlersIntTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val clock: MutableClock,
    @Autowired val testService: TestService
) {

    val correlationId = UUID.randomUUID().toString()

    @TestConfiguration
    class AdditionalBeans {
        @Bean fun correlationIdHolder() = CorrelationIdHolder()
        @Bean fun testService(): TestService = mockk()
    }

    @BeforeEach fun setTime() {
        clock.setFixedTime("2017-09-01T12:34:56.789Z")
    }

    @Test fun `NotFoundException is handled`() {
        executionWillThrow { NotFoundException("something was not found") }
        executeAndExpect(NOT_FOUND) {
            """
            {
              "status": 404,
              "error": "Not Found",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "something was not found"
            }
            """
        }
    }

    @Test fun `NotPossibleException is handled`() {
        executionWillThrow { NotPossibleException("something could not be done") }
        executeAndExpect(CONFLICT) {
            """
            {
              "status": 409,
              "error": "Conflict",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "something could not be done"
            }
            """
        }
    }

    @Test fun `MalformedValueException is handled`() {
        executionWillThrow { MalformedValueException("some value was wrong") }
        executeAndExpect(BAD_REQUEST) {
            """
            {
              "status": 400,
              "error": "Bad Request",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "some value was wrong"
            }
            """
        }
    }

    @Test fun `MethodArgumentTypeMismatchException is handled`() {
        executionWillThrow {
            MethodArgumentTypeMismatchException("value", String::class.java, "myArgument", mockk(), mockk())
        }
        executeAndExpect(BAD_REQUEST) {
            """
            {
              "status": 400,
              "error": "Bad Request",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "The request's 'myArgument' parameter is malformed."
            }
            """
        }
    }

    @Test fun `HttpMessageNotReadableException is handled`() {
        executionWillThrow { HttpMessageNotReadableException("this will not be exposed", mockk<HttpInputMessage>()) }
        executeAndExpect(BAD_REQUEST) {
            """
            {
              "status": 400,
              "error": "Bad Request",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "The request's body could not be read. It is either empty or malformed."
            }
            """
        }
    }

    @Test fun `MethodArgumentNotValidException is handled`() {
        val bindingResult = bindingResult()
        executionWillThrow { MethodArgumentNotValidException(mockk(), bindingResult) }
        executeAndExpect(BAD_REQUEST) {
            """
            {
              "status": 400,
              "error": "Bad Request",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "The request's body is invalid. See details...",
              "details": [
                "Gloabl Message 1",
                "Gloabl Message 2",
                "The field 'field1' Message about field1.",
                "The field 'field2' Message about field2."
              ]
            }
            """
        }
    }

    private fun bindingResult(): BindingResult {
        val fieldError1 = FieldError("objectName", "field1", "Message about field1")
        val fieldError2 = FieldError("objectName", "field2", "Message about field2")
        val globalError1 = ObjectError("objectName", "Gloabl Message 1")
        val globalError2 = ObjectError("objectName", "Gloabl Message 2")

        return mockk() {
            every { fieldErrors } returns listOf(fieldError1, fieldError2)
            every { globalErrors } returns listOf(globalError1, globalError2)
        }
    }

    @Test fun `AccessDeniedException is handled`() {
        executionWillThrow { AccessDeniedException("missing right") }
        executeAndExpect(FORBIDDEN) {
            """
            {
              "status": 403,
              "error": "Forbidden",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "You don't have the necessary rights to to this."
            }
            """
        }
    }

    @Test fun `Exception is handled`() {
        executionWillThrow { Exception("this will not be exposed") }
        executeAndExpect(INTERNAL_SERVER_ERROR) {
            """
            {
              "status": 500,
              "error": "Internal Server Error",
              "timestamp": "2017-09-01T12:34:56.789Z",
              "correlationId": "$correlationId",
              "message": "An internal server error occurred, see server logs for more information."
            }
            """
        }
    }

    private fun executionWillThrow(exceptionSupplier: () -> Throwable) {
        every { testService.doSomething() } throws exceptionSupplier()
    }

    private fun executeAndExpect(expectedStatus: HttpStatus, expectedResponseSupplier: () -> String) {
        val request = post("/test")
            .header("X-Correlation-ID", correlationId)
        mockMvc.perform(request)
            .andExpect(status().`is`(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(content().json(expectedResponseSupplier(), true))
    }

}