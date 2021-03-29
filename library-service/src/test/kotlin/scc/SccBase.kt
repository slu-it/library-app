package scc

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc
import library.service.api.books.BookResource
import library.service.api.books.BookResourceAssembler
import library.service.api.books.BooksController
import library.service.business.books.BookCollection
import library.service.business.books.domain.BookRecord
import library.service.security.UserContext
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.web.context.WebApplicationContext
import utils.Books
import utils.ResetMocksAfterEachTest

//@Provider("library-service")
//@PactFolder("src/test/pacts/http")
//@VerificationReports("console")
//@WebMvcTest
@ResetMocksAfterEachTest
@TestInstance(PER_CLASS)
open class SccBase {

//    @Autowired
//    lateinit var context: WebApplicationContext

//    @BeforeAll
//    fun setup() {
//        RestAssuredMockMvc.webAppContextSetup(context)
//    }

    @BeforeAll
    fun setup() {
        val coll = mockk<BookCollection>()

        every { coll.updateBook(any(), any()) } answers {
            BookRecord(firstArg(), Books.THE_MARTIAN)
        }

        val user = mockk<UserContext>(relaxed = true)
        val ass = BookResourceAssembler(user)

        RestAssuredMockMvc.standaloneSetup(
            BooksController(coll, ass)
        )
    }
}