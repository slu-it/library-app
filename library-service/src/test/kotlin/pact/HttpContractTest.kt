package pact

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import io.mockk.every
import io.mockk.mockk
import library.service.Application
import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookEvent
import library.service.business.books.domain.types.BookId
import library.service.business.events.EventDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import utils.Books
import utils.ResetMocksAfterEachTest
import utils.classification.ContractTest

@ContractTest
@ResetMocksAfterEachTest
@Provider("library-service")
@PactFolder("src/test/pacts/http")
@VerificationReports("console")
@SpringBootTest(
    classes = [Application::class, HttpContractTest.AdditionalBeans::class],
    webEnvironment = RANDOM_PORT,
    properties = ["application.secured=false"]
)
@TestInstance(PER_CLASS) // PACT needs this ... for some reason ...
class HttpContractTest(
    @Autowired val dataStore: BookDataStore
) {

    class AdditionalBeans {
        @Primary @Bean fun bookDataStore(): BookDataStore = mockk()
        @Primary @Bean fun eventDispatcher(): EventDispatcher<BookEvent> = mockk(relaxed = true)
    }

    @BeforeEach
    fun setTarget(context: PactVerificationContext, @LocalServerPort port: Int) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("A book with the ID {bookId} exists")
    fun `book with fixed ID exists`(params: Map<String, String>) {
        val bookId = BookId.from(params["bookId"]!!)
        val bookRecord = BookRecord(bookId, Books.THE_MARTIAN)
        every { dataStore.findById(bookId) } returns bookRecord
        every { dataStore.createOrUpdate(any()) } answers { firstArg() }
    }

}