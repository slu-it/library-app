package library.service.business.books.domain.events

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.BookId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import utils.Books
import utils.classification.UnitTest
import java.time.OffsetDateTime
import java.util.*

@UnitTest
internal class BookEventsTest {

    val uuid = UUID.randomUUID()!!
    val timestamp = OffsetDateTime.now()!!
    val bookRecord = BookRecord(BookId.generate(), Books.THE_MARTIAN)

    val allBookEventTypes = listOf(
            BookAdded(uuid, timestamp, bookRecord),
            BookUpdated(uuid, timestamp, bookRecord),
            BookRemoved(uuid, timestamp, bookRecord),
            BookBorrowed(uuid, timestamp, bookRecord),
            BookReturned(uuid, timestamp, bookRecord)
    )

    @Test fun `all event types are unique`() {
        val types = allBookEventTypes.map { it.type }
        val distinctTypes = types.distinct()
        assertThat(types).isEqualTo(distinctTypes)
    }

    @TestFactory fun `all event types follow naming pattern`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.type).matches("[a-z]+(-[a-z]+)*")
        }
    }

    @TestFactory fun `all event ids are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.id).isEqualTo(uuid.toString())
        }
    }

    @TestFactory fun `all event book ids are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.bookId).isEqualTo(bookRecord.id.toString())
        }
    }

    @TestFactory fun `all event timestamps are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.timestamp).isEqualTo(timestamp.toString())
        }
    }

    @TestFactory fun `all event book isbn are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.isbn).isEqualTo(bookRecord.book.isbn.toString())
        }
    }

    @TestFactory fun `all event book titles are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.title).isEqualTo(bookRecord.book.title.toString())
        }
    }

}