package contractbase

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup
import library.service.Application
import library.service.api.books.BookResourceAssembler
import library.service.business.books.BookCollection
import library.service.business.books.domain.BookRecord
import library.service.correlation.CorrelationIdHolder
import library.service.correlation.CorrelationIdServletFilter
import library.service.security.UserContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import utils.Books.THE_MARTIAN
import utils.ResetMocksAfterEachTest

private val bookCollection: BookCollection = mockk()

@ResetMocksAfterEachTest
@TestInstance(PER_CLASS)
@Import(SccBaseConfiguration::class)
@WebMvcTest(properties = ["application.secured=false"])
open class HttpBase {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var correlationIdServletFilter: CorrelationIdServletFilter

    @BeforeAll
    fun setup() = standaloneSetup(
        webAppContextSetup(context)
            .addFilters(correlationIdServletFilter)
    )

    @BeforeEach
    fun stubBookCollection() {
        every { bookCollection.updateBook(any(), any()) } answers { BookRecord(firstArg(), THE_MARTIAN) }
    }
}

@Import(
    UserContext::class,
    CorrelationIdHolder::class,
    BookResourceAssembler::class,
)

private class SccBaseConfiguration {
    @Bean
    fun bookCollection(): BookCollection = bookCollection
}
