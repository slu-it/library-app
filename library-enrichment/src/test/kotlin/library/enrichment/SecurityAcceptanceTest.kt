package library.enrichment

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import library.enrichment.gateways.library.LibraryClient
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.classification.AcceptanceTest
import utils.extensions.UseDockerToRunRabbitMQ

@AcceptanceTest
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
internal class SecurityAcceptanceTest {

    @MockBean lateinit var libraryClient: LibraryClient

    @LocalServerPort
    fun setupRestAssured(port: Int) {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    @Nested inner class `actuator endpoints` {

        @Test fun `actuator info endpoint can be accessed by anyone`() {
            given { auth().none() } `when` { get("/actuator/info") } then { statusCode(200) }
        }

        @Test fun `actuator health endpoint can be accessed by anyone`() {
            // status 503 because RabbitMQ won't be available
            given { auth().none() } `when` { get("/actuator/health") } then { statusCode(503) }
        }

        @ValueSource(strings = ["beans", "conditions", "configprops", "env", "loggers",
            "metrics", "scheduledtasks", "httptrace", "mappings"])
        @ParameterizedTest fun `any other actuator endpoint can only be accessed by an admin`(endpoint: String) {
            given { auth().none() }
                    .`when` { get("/actuator/$endpoint") }
                    .then { statusCode(401) }
            given { auth().basic("admin", "admin") }
                    .`when` { get("/actuator/$endpoint") }
                    .then { statusCode(200) }
        }

    }

    private fun given(body: RequestSpecification.() -> RequestSpecification): RequestSpecification {
        return body(given())
    }

    private infix fun RequestSpecification.`when`(body: RequestSpecification.() -> Response): Response {
        return body(this.`when`())
    }

    private infix fun Response.`then`(body: ValidatableResponse.() -> ValidatableResponse): ValidatableResponse {
        return body(this.then())
    }

}