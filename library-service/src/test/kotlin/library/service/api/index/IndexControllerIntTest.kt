package library.service.api.index

import library.service.common.correlation.CorrelationIdHolder
import library.service.security.UserContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.hateoas.MediaTypes.HAL_JSON_UTF8
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.classification.IntegrationTest
import utils.document


@IntegrationTest
@ExtendWith(SpringExtension::class)
@WebMvcTest(IndexController::class, secure = false)
@AutoConfigureRestDocs("build/generated-snippets/index")
internal class IndexControllerIntTest {

    @SpyBean lateinit var correlationIdHolder: CorrelationIdHolder
    @SpyBean lateinit var userContext: UserContext

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `get api index returns links to available endpoint actions`() {
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
                .andExpect(content().contentType(HAL_JSON_UTF8))
                .andExpect(content().json(expectedResponse, true))
                .andDo(document("getIndex"))
    }

}