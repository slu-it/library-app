package pact

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.willReturn
import library.service.Application
import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookEvent
import library.service.business.books.domain.types.BookId
import library.service.business.events.EventDispatcher
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testit.pact.provider.junit.PactFileLoader
import org.testit.pact.provider.junit.http.ProviderState
import org.testit.pact.provider.junit.http.RequestResponsePactTestFactory
import utils.Books
import utils.classification.ContractTest

@ContractTest
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [Application::class], webEnvironment = RANDOM_PORT)
@ActiveProfiles("test", "unsecured")
class HttpContractTest {

    @MockBean lateinit var dataStore: BookDataStore
    @MockBean lateinit var eventDispatcher: EventDispatcher<BookEvent>

    val testFactory = RequestResponsePactTestFactory(PactFileLoader("src/test/pacts/http"), "library-service")

    @LocalServerPort
    fun init(port: Int) {
        testFactory.httpTarget.port = { port }
    }

    @TestFactory fun `library enrichment contract tests`() =
            testFactory.createTests("library-enrichment", this)

    @ProviderState("A book with the ID {bookId} exists")
    fun `book with fixed ID exists`(params: Map<String, String>) {
        val bookId = BookId.from(params["bookId"]!!)
        val bookRecord = BookRecord(bookId, Books.THE_MARTIAN)
        given { dataStore.findById(bookId) }.willReturn { bookRecord }
        given { dataStore.createOrUpdate(any()) }.willAnswer { it.arguments[0] as BookRecord }
    }

}