package library.service.api

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.willThrow
import library.service.api.ErrorHandlersIntTest.CustomTestConfiguration.TestController
import library.service.api.ErrorHandlersIntTest.CustomTestConfiguration.TestService
import library.service.business.exceptions.MalformedValueException
import library.service.business.exceptions.NotFoundException
import library.service.business.exceptions.NotPossibleException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import utils.classification.IntegrationTest
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*


@IntegrationTest
@ExtendWith(SpringExtension::class)
@WebMvcTest(TestController::class, secure = false)
@ActiveProfiles("test", "error-handlers-test")
internal class ErrorHandlersIntTest {

    @TestConfiguration
    @Profile("error-handlers-test")
    @ComponentScan("library.service.common")
    class CustomTestConfiguration {

        @Bean fun clock(): Clock = Clock.fixed(OffsetDateTime.parse("2017-09-01T12:34:56.789Z").toInstant(), ZoneId.of("UTC"))

        @RestController
        @Profile("error-handlers-test")
        class TestController(private val testService: TestService) {
            @PostMapping("/test")
            fun post() = testService.doSomething()
        }

        interface TestService {
            @Throws(Throwable::class)
            fun doSomething()
        }

    }

    val correlationId = UUID.randomUUID().toString()

    @MockBean lateinit var testService: TestService
    @Autowired lateinit var mockMvc: MockMvc

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
        executionWillThrow { MethodArgumentTypeMismatchException("value", String::class.java, "myArgument", mock(), mock()) }
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
        executionWillThrow { HttpMessageNotReadableException("this will not be exposed") }
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
        executionWillThrow { MethodArgumentNotValidException(mock(), bindingResult) }
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

        return mock {
            on { fieldErrors } doReturn listOf(fieldError1, fieldError2)
            on { globalErrors } doReturn listOf(globalError1, globalError2)
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
        given { testService.doSomething() } willThrow (exceptionSupplier)
    }

    private fun executeAndExpect(expectedStatus: HttpStatus, expectedResponseSupplier: () -> String) {
        mockMvc.perform(post("/test")
                .header("X-Correlation-ID", correlationId))
                .andExpect(status().`is`(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(expectedResponseSupplier(), true))
    }

}