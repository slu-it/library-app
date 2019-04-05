package library.service.security

import library.service.correlation.CorrelationIdHolder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import utils.classification.IntegrationTest


@IntegrationTest
@WebMvcTest(UserInfoController::class, secure = true)
internal class UserInfoControllerIntTest {

    @TestConfiguration
    @Import(SecurityConfiguration::class)
    class AdditionalBeans {
        @Bean fun correlationIdHolder() = CorrelationIdHolder()
    }

    @Autowired lateinit var mockMvc: MockMvc

    @Test fun `GET returns user info for authenticated user`() {
        val request = get("/userinfo")
            .with(httpBasic("curator", "curator"))
        val expectedResponse = """
                {
                  "username": "curator",
                  "authorities": ["ROLE_CURATOR", "ROLE_USER"]
                }
            """
        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().json(expectedResponse, true))
    }

    @Test fun `GET responds with 401 for wrong user credentials`() {
        val request = get("/userinfo")
            .with(httpBasic("curator", "wrong"))
        mockMvc.perform(request)
            .andExpect(status().isUnauthorized)
    }

}