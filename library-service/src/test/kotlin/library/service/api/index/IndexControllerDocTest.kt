package library.service.api.index

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import library.service.common.correlation.CorrelationIdHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.classification.IntegrationTest

@IntegrationTest
@WebMvcTest(IndexController::class, secure = false)
@ExtendWith(SpringExtension::class)
@AutoConfigureRestDocs("build/generated-snippets/index")
internal class IndexControllerDocTest {

    @SpyBean lateinit var correlationIdHolder: CorrelationIdHolder

    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var mvc: MockMvc

    @BeforeEach fun setUp() {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
    }

    // GET on /api

    @Test fun `getting index`() {
        mvc.perform(get("/api"))
                .andExpect(status().isOk)
                .andDo(document("getIndex"))
    }

    // utility methods

    private fun document(identifier: String, vararg snippets: Snippet): RestDocumentationResultHandler {
        return document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), *snippets)
    }

}