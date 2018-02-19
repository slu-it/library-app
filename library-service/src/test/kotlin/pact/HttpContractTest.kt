package pact

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit.target.HttpTarget
import au.com.dius.pact.provider.junit.target.Target
import au.com.dius.pact.provider.junit.target.TestTarget
import au.com.dius.pact.provider.spring.SpringRestPactRunner
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.willReturn
import library.service.Application
import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookEvent
import library.service.business.books.domain.types.BookId
import library.service.business.events.EventDispatcher
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import utils.Books
import utils.classification.ContractTest

@ContractTest
@RunWith(SpringRestPactRunner::class)
@Provider("library-service")
@PactFolder("src/test/pacts/http")
@VerificationReports("console")
@SpringBootTest(classes = [Application::class], webEnvironment = RANDOM_PORT)
@ActiveProfiles("test", "unsecured")
class HttpContractTest {

    @MockBean lateinit var dataStore: BookDataStore
    @MockBean lateinit var eventDispatcher: EventDispatcher<BookEvent>

    @TestTarget lateinit var target: Target

    @LocalServerPort
    fun init(port: Int) {
        target = HttpTarget("http", "localhost", port, "/", true)
    }

    @State("A book with the ID {bookId} exists")
    fun `book with fixed ID exists`(params: Map<String, String>) {
        val bookId = BookId.from(params["bookId"]!!)
        val bookRecord = BookRecord(bookId, Books.THE_MARTIAN)
        given { dataStore.findById(bookId) }.willReturn { bookRecord }
        given { dataStore.createOrUpdate(any()) }.willAnswer { it.arguments[0] as BookRecord }
    }

}