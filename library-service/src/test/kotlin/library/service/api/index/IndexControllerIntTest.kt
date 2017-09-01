package library.service.api.index

import library.service.api.ErrorHandlers
import library.service.common.correlation.CorrelationIdHolder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.IntegrationTest
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId


@WebMvcTest
@IntegrationTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = arrayOf(IndexControllerIntTest.TestConfiguration::class))
internal class IndexControllerIntTest {

    @ComponentScan("library.service.api.index", "library.service.common")
    class TestConfiguration {
        @Bean fun clock(): Clock = Clock.fixed(OffsetDateTime.parse("2017-09-01T12:34:56.789Z").toInstant(), ZoneId.of("UTC"))
        @Bean fun errorHandlers(clock: Clock, correlationIdHolder: CorrelationIdHolder) = ErrorHandlers(clock, correlationIdHolder)
    }

    val APPLICATION_HAL_JSON = "application/hal+json;charset=UTF-8"

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `get index returns links to available endpoint actions`() {
        val request = get("/api")
        val expectedResponse = """
                {
                  "_links": {
                    "self": { "href": "http://localhost/api" },
                    "getBooks": { "href": "http://localhost/api/books" },
                    "addBook": { "href": "http://localhost/api/books" }
                  }
                }
            """
        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(content().json(expectedResponse, true))
    }

}