package library.service.api.index

import io.mockk.every
import io.mockk.mockk
import library.service.correlation.CorrelationIdHolder
import library.service.security.UserContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.classification.IntegrationTest
import utils.document


@IntegrationTest
@WebMvcTest(IndexController::class)
@AutoConfigureRestDocs("build/generated-snippets/index")
internal class IndexControllerIntTest(
    @Autowired val mockMvc: MockMvc
) {

    @TestConfiguration
    class AdditionalBeans {
        @Bean
        fun userContext() = mockk<UserContext> {
            every { isCurator() } returns true
        }

        @Bean
        fun correlationIdHolder() = CorrelationIdHolder()
    }

    @Test
    fun `get api index returns links to available endpoint actions`() {
        val request = get("/api")
        val expectedResponse = """
                {
                  "_links": {
                    "self": { "href": "http://localhost:8080/api" },
                    "getBooks": { "href": "http://localhost:8080/api/books" },
                    "addBook": { "href": "http://localhost:8080/api/books" }
                  }
                }
            """
        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(HAL_JSON))
            .andExpect(content().json(expectedResponse, true))
            .andDo(document("getIndex"))
    }

}