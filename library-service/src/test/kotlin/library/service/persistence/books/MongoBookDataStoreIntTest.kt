package library.service.persistence.books

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.classification.IntegrationTest
import utils.extensions.UseDockerToRunMongoDB
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DataMongoTest
@IntegrationTest
@UseDockerToRunMongoDB
@ExtendWith(SpringExtension::class)
internal class MongoBookDataStoreIntTest {

    @Autowired lateinit var repository: BookRepository
    lateinit var cut: MongoBookDataStore

    @BeforeEach fun createCut() {
        cut = MongoBookDataStore(repository)
    }

    @AfterEach fun deleteAllBooks() {
        repository.deleteAll()
    }

    @Test fun `books can be created`() {
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = cut.create(book)

        assertThat(bookEntity.id).isNotNull()
        assertThat(bookEntity.book).isEqualTo(book)
        assertThat(bookEntity.state).isEqualTo(Available)
    }

    @Test fun `books can be looked up by their ID`() {
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = cut.create(book)

        val foundBook = cut.findById(bookEntity.id)
        assertThat(foundBook).isEqualToComparingFieldByField(bookEntity)
    }

    @Test fun `books looked up by their ID might not exist`() {
        val foundBook = cut.findById(BookId.generate())
        assertThat(foundBook).isNull()
    }

    @Test fun `books can be deleted`() {
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = cut.create(book)

        cut.delete(bookEntity)

        val foundBook = cut.findById(bookEntity.id)
        assertThat(foundBook).isNull()
    }

    @Test fun `all books can be looked up at once`() {
        val bookEntity1 = cut.create(Book(Isbn13("0123456789012"), Title("Hello World #1")))
        val bookEntity2 = cut.create(Book(Isbn13("1234567890123"), Title("Hello World #2")))
        val bookEntity3 = cut.create(Book(Isbn13("2345678901234"), Title("Hello World #3")))

        val allBooks = cut.findAll().sortedBy { it.book.isbn.toString() }

        assertThat(allBooks[0]).isEqualToComparingFieldByField(bookEntity1)
        assertThat(allBooks[1]).isEqualToComparingFieldByField(bookEntity2)
        assertThat(allBooks[2]).isEqualToComparingFieldByField(bookEntity3)
    }

    @Test fun `books can be updated - to borrowed state`() {
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = cut.create(book)

        val borrower = Borrower("Some One")
        val now = OffsetDateTime.now()
        bookEntity.borrow(borrower, now)

        val updatedBookEntity = cut.update(bookEntity)

        assertThat(updatedBookEntity).isNotSameAs(bookEntity)
        assertThat(updatedBookEntity.id).isEqualTo(bookEntity.id)
        assertThat(updatedBookEntity.book).isEqualTo(bookEntity.book)

        val borrowed = updatedBookEntity.state as Borrowed
        assertThat(borrowed.by).isEqualTo(borrower)
        assertThat(borrowed.on).isEqualTo(now.withOffsetSameInstant(ZoneOffset.UTC))
    }

    @Test fun `books can be updated - to returned state`() {
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = cut.create(book)

        val updatedBookEntity = cut.update(bookEntity)

        assertThat(updatedBookEntity).isNotSameAs(bookEntity)
        assertThat(updatedBookEntity.id).isEqualTo(bookEntity.id)
        assertThat(updatedBookEntity.book).isEqualTo(bookEntity.book)
        assertThat(updatedBookEntity.state).isEqualTo(Available)
    }

}