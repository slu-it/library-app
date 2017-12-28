package library.service.business.books.domain.events

import library.service.business.books.domain.types.BookId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.OffsetDateTime
import java.util.*


internal class BookEventsTest {

    val uuid = UUID.randomUUID()!!
    val bookId = BookId.generate()
    val timestamp = OffsetDateTime.now()!!

    val allBookEventTypes = listOf(
            BookAdded(uuid, bookId, timestamp),
            BookRemoved(uuid, bookId, timestamp),
            BookBorrowed(uuid, bookId, timestamp),
            BookReturned(uuid, bookId, timestamp)
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
            assertThat(it.bookId).isEqualTo(bookId.toString())
        }
    }

    @TestFactory fun `all event timestamps are formatted as strings`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            assertThat(it.timestamp).isEqualTo(timestamp.toString())
        }
    }

}