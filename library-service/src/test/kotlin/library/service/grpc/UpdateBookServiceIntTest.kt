package library.service.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import library.service.business.books.BookCollection
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.types.Author
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.security.SecurityCommons
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import utils.classification.IntegrationTest

@IntegrationTest
@SpringBootTest(
    properties = [
        "grpc.server.port=$GRPC_SERVER_PORT"
    ]
)
@Import(GrpcServer::class)
@ContextConfiguration(classes = [UpdateBookServiceIntTest.TestConfiguration::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateBookServiceIntTest {

    @Autowired
    private lateinit var mapper: BookResponseMapper

    @Autowired
    private lateinit var securityCommons: SecurityCommons

    @Autowired
    private lateinit var grpcServer: GrpcServer

    val captureCallback = slot<() -> BookRecord>()

    private lateinit var server: Server

    private lateinit var channel: ManagedChannel

    @BeforeAll
    fun setUp() {
        /**
         * Initializes and starts a gRPC Server
         */
        server = grpcServer.init()
        grpcServer.start(server)

        /**
         * Creates a channel that will connect to the gRPC Server
         */
        channel = ManagedChannelBuilder
            .forAddress("localhost", GRPC_SERVER_PORT)
            .usePlaintext()
            .build()
    }

    @AfterAll
    fun closeResources() {
        channel.shutdownNow()
        server.shutdownNow()
    }

    class TestConfiguration {
        @Bean
        fun bookCollection() = mockk<BookCollection>()

        @Bean
        fun securityCommons() = mockk<SecurityCommons>()

        @Bean
        fun createBookResponseMapper() = mockk<BookResponseMapper>()

        @Bean
        fun updateBookService(
            collection: BookCollection, mapper: BookResponseMapper,
            securityCommons: SecurityCommons
        ) = UpdateBookService(collection, mapper, securityCommons)

        @Bean
        fun getBookService() = mockk<GetBooksService>(relaxed = true)

        @Bean
        fun createBookService() = mockk<CreateBookService>(relaxed = true)
    }

    @Test
    fun `should update authors given valid UpdateAuthorsRequest`() {

        val clientStub = UpdateBookGrpcKt.UpdateBookCoroutineStub(channel)

        val title = "HarryPotter"
        val isbn = "9783551557414"
        val bookId = BookId.generate()

        val bookRecord = BookRecord(
            id = bookId,
            book = Book(
                isbn = Isbn13(isbn),
                title = Title(title),
                authors = listOf(Author("J.K. Rowling")),
                numberOfPages = 257
            ),
            state = Available
        )

        val request = UpdateAuthorsRequest.newBuilder().apply {
            this.addAllAuthors(listOf("J.K. Rowling"))
            this.bookId = bookId.toString()
        }.build()

        val expectedResponse = BookResponse
            .newBuilder()
            .apply {
                this.isbn = isbn
                this.title = title
                this.addAllAuthors(listOf("J.K. Rowling"))
                this.numberOfPages = 257
                this.borrowed = Borrowed.newBuilder().build()
            }
            .build()

        every { securityCommons.executeOperationAsCurator(capture(captureCallback)) } answers { bookRecord }
        every { mapper.toBookResponse(bookRecord) } returns expectedResponse

        val result = runBlocking {
            clientStub.updateAuthors(request)
        }

        assertThat(result).isEqualTo(expectedResponse)
    }

    @Test
    fun `should update number of pages given valid UpdateNumberOfPagesRequest`() {

        val clientStub = UpdateBookGrpcKt.UpdateBookCoroutineStub(channel)

        val title = "HarryPotter"
        val isbn = "9783551557414"
        val bookId = BookId.generate()

        val bookRecord = BookRecord(
            id = bookId,
            book = Book(
                isbn = Isbn13(isbn),
                title = Title(title),
                authors = listOf(Author("J.K. Rowling")),
                numberOfPages = 257
            ),
            state = Available
        )

        val request = UpdateNumberOfPagesRequest.newBuilder().apply {
            this.numberOfPages = 257
            this.bookId = bookId.toString()
        }.build()

        val expectedResponse = BookResponse
            .newBuilder()
            .apply {
                this.isbn = isbn
                this.title = title
                this.addAllAuthors(listOf("J.K. Rowling"))
                this.numberOfPages = 257
                this.borrowed = Borrowed.newBuilder().build()
            }
            .build()

        every { securityCommons.executeOperationAsCurator(capture(captureCallback)) } answers { bookRecord }
        every { mapper.toBookResponse(bookRecord) } returns expectedResponse

        val result = runBlocking {
            clientStub.updateNumberOfPages(request)
        }

        assertThat(result).isEqualTo(expectedResponse)
    }
}
