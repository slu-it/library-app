package library.service.persistence.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.Books
import utils.classification.IntegrationTest
import utils.extensions.UseDockerToRunMongoDB
import java.time.OffsetDateTime
import java.time.ZoneOffset

@IntegrationTest
@ExtendWith(SpringExtension::class)
@DataMongoTest
@UseDockerToRunMongoDB
@ComponentScan("library.service.persistence.books")
internal class MongoBookDataStoreIntTest {

    @Autowired lateinit var repository: BookRepository
    @Autowired lateinit var cut: MongoBookDataStore

    @BeforeEach fun resetDatabase() = with(repository) { deleteAll() }

    @Test fun `books are created as available records`() {
        with(cut.create(Books.THE_DARK_TOWER_I)) {
            assertThat(id).isNotNull()
            assertThat(book).isEqualTo(Books.THE_DARK_TOWER_I)
            assertThat(state).isEqualTo(Available)
        }
    }

    @Test fun `book records can be looked up by their ID`() {
        val bookRecord = cut.create(Books.THE_DARK_TOWER_II)
        assertThat(cut.findById(bookRecord.id)).isEqualTo(bookRecord)
    }

    @Test fun `looking up book records by their ID might return null`() {
        assertThat(cut.findById(BookId.generate())).isNull()
    }

    @Test fun `book records can be deleted`() {
        val bookRecord = cut.create(Books.THE_DARK_TOWER_III)
        cut.delete(bookRecord)
        assertThat(cut.findById(bookRecord.id)).isNull()
    }

    @Test fun `all book records can be looked up at once`() {
        val bookRecord1 = cut.create(Books.THE_DARK_TOWER_IV)
        val bookRecord2 = cut.create(Books.THE_DARK_TOWER_V)
        val bookRecord3 = cut.create(Books.THE_DARK_TOWER_VI)

        val allBooks = cut.findAll()

        assertThat(allBooks).containsOnly(bookRecord1, bookRecord2, bookRecord3)
    }

    @Test fun `book records can be updated`() {
        val originalRecord = cut.create(Books.THE_DARK_TOWER_VII)

        val borrowedBy = Borrower("Frodo")
        val borrowedOn = OffsetDateTime.now()
        val modifiedRecord = BookRecord(originalRecord.id, Books.THE_LORD_OF_THE_RINGS_1, Borrowed(borrowedBy, borrowedOn))

        with(cut.update(modifiedRecord)) {
            assertThat(id).isEqualTo(originalRecord.id)
            assertThat(book).isEqualTo(Books.THE_LORD_OF_THE_RINGS_1)
            with(state as Borrowed) {
                assertThat(by).isEqualTo(borrowedBy)
                assertThat(on).isEqualTo(borrowedOn.withOffsetSameInstant(ZoneOffset.UTC))
            }
        }
    }

}