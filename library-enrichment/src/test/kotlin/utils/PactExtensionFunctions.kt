package utils

import au.com.dius.pact.consumer.*
import au.com.dius.pact.consumer.dsl.PactDslResponse
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.model.MockProviderConfig
import au.com.dius.pact.model.PactSpecVersion
import au.com.dius.pact.model.RequestResponsePact
import org.assertj.core.api.Assertions.assertThat


fun setPactContractFolder(folder: String) {
    System.setProperty("pact.rootDir", folder)
}

fun pactWith(provider: String, body: PactDslWithProvider.() -> PactDslResponse): RequestResponsePact {
    return body(ConsumerPactBuilder.consumer("library-enrichment").hasPactWith(provider)).toPact()
}

infix fun RequestResponsePact.execute(test: (MockServer) -> Unit) {
    val config = MockProviderConfig.createDefault(PactSpecVersion.V3)
    val result = runConsumerTest(this, config, TestRun(test))
    if (result is PactVerificationResult.Error) {
        throw AssertionError(result.error)
    }
    assertThat(result).isEqualTo(PactVerificationResult.Ok)
}

private class TestRun(private val test: (MockServer) -> Unit) : PactTestRun {
    override fun run(mockServer: MockServer, context: PactTestExecutionContext?) {
        test(mockServer)
    }
}
