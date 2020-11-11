package library.service.grpc

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Server
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import library.service.business.books.BookCollection
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
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
import utils.Books.THE_LORD_OF_THE_RINGS_1
import utils.Books.THE_LORD_OF_THE_RINGS_2
import utils.Books.THE_LORD_OF_THE_RINGS_3
import utils.classification.IntegrationTest
import java.time.OffsetDateTime
import library.service.business.books.domain.states.Borrowed as BorrowedState

const val GRPC_SERVER_PORT = 50057

@IntegrationTest
@SpringBootTest(
    properties = [
        "grpc.server.port=$GRPC_SERVER_PORT"
    ]
)
@Import(GrpcServer::class)
@ContextConfiguration(classes = [GetBooksServiceIntTest.TestConfiguration::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetBooksServiceIntTest {

    @Autowired
    private lateinit var mapper: BookResponseMapper

    @Autowired
    private lateinit var securityCommons: SecurityCommons

    @Autowired
    private lateinit var grpcServer: GrpcServer

    val captureCallback = slot<() -> List<BookRecord>>()

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
        fun bookResponseMapper() = mockk<BookResponseMapper>()

        @Bean
        fun getBooksService(
            collection: BookCollection, mapper: BookResponseMapper,
            securityCommons: SecurityCommons
        ) = GetBooksService(collection, mapper, securityCommons)

        @Bean
        fun createBookService() = mockk<CreateBookService>(relaxed = true)

        @Bean
        fun updateBookService() = mockk<UpdateBookService>(relaxed = true)
    }

    @Test
    fun `should return stream of all books, given at least one book exists in the book collection`() {

        val clientStub = GetBooksGrpcKt.GetBooksCoroutineStub(channel)

        val firstBookRecordAvailable = BookRecord(
            id = BookId.generate(),
            book = THE_LORD_OF_THE_RINGS_1,
            state = Available
        )
        val secondBookRecordAvailable = BookRecord(
            id = BookId.generate(),
            book = THE_LORD_OF_THE_RINGS_2,
            state = Available
        )
        val thirdBookRecordBorrowed = BookRecord(
            id = BookId.generate(),
            book = THE_LORD_OF_THE_RINGS_3,
            state = BorrowedState(by = Borrower("Test Name"), on = OffsetDateTime.parse("2020-09-12T10:30:00.789Z"))
        )

        val allBooks = listOf(firstBookRecordAvailable, secondBookRecordAvailable, thirdBookRecordBorrowed)

        val firstBookResponse = BookResponse
            .newBuilder()
            .apply {
                this.isbn = "9780261102354"
                this.title = "The Lord of the Rings 1. The Fellowship of the Ring"
                this.addAllAuthors(listOf("J.R.R. Tolkien"))
                this.numberOfPages = 529
                this.borrowed = Borrowed.newBuilder().build()
            }
            .build()

        val secondBookResponse = BookResponse
            .newBuilder()
            .apply {
                this.isbn = "9780261102361"
                this.title = "The Lord of the Rings 2. The Two Towers"
                this.addAllAuthors(listOf("J.R.R. Tolkien"))
                this.numberOfPages = 442
                this.borrowed = Borrowed.newBuilder().build()
            }
            .build()

        val thirdBookResponse = BookResponse
            .newBuilder()
            .apply {
                this.isbn = "9780261102378"
                this.title = "The Lord of the Rings 3. The Return of the King"
                this.addAllAuthors(listOf("J.R.R. Tolkien"))
                this.numberOfPages = 556
                this.borrowed = Borrowed.newBuilder().apply {
                    this.by = "Test Name"
                    this.on = "2020-09-12T10:30:00.789Z"
                }
                    .build()
            }
            .build()

        val expectedResponse = flowOf(firstBookResponse, secondBookResponse, thirdBookResponse)

        every { securityCommons.executeOperationAsUser(capture(captureCallback)) } answers { allBooks }
        every { mapper.toBooksResponse(allBooks) } returns expectedResponse

        val result = runBlocking {
            clientStub.getBooks(Empty.newBuilder().build())
        }

        val resultAsList = runBlocking {
            result.toList()
        }

        assertThat(resultAsList.size).isEqualTo(3)
        assertThat(resultAsList).containsExactly(firstBookResponse, secondBookResponse, thirdBookResponse)
    }

    @Test
    fun `should return empty stream of books, given no book exists in the book collection`() {

        val clientStub = GetBooksGrpcKt.GetBooksCoroutineStub(channel)

        val allBooks = listOf<BookRecord>()

        val expectedResponse = flowOf<BookResponse>()

        every { securityCommons.executeOperationAsUser(capture(captureCallback)) } answers { allBooks }
        every { mapper.toBooksResponse(allBooks) } returns expectedResponse

        val result = runBlocking {
            clientStub.getBooks(Empty.newBuilder().build())
        }

        val resultAsList = runBlocking {
            result.toList()
        }

        assertThat(resultAsList).isEmpty()
    }
}
