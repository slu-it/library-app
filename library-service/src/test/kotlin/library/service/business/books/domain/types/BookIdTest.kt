package library.service.business.books.domain.types

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.UnitTest
import java.util.*

@UnitTest
internal class BookIdTest {

    val uuid = UUID.fromString("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")

    @Test fun `toString() returns BookId's value as a String`() {
        val bookId = BookId(uuid)
        assertThat(bookId.toString()).isEqualTo("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")
    }

    @Test fun `toUuid() returns BookId's value as a UUID`() {
        val bookId = BookId(uuid)
        assertThat(bookId.toUuid()).isEqualTo(uuid)
    }

    @Test fun `when generating IDs each generated ID is unique`() {
        val bookId1 = BookId.generate()
        val bookId2 = BookId.generate()

        assertThat(bookId1).isEqualTo(bookId1)
        assertThat(bookId2).isEqualTo(bookId2)

        assertThat(bookId1).isNotEqualTo(bookId2)
        assertThat(bookId1.hashCode()).isNotEqualTo(bookId2.hashCode())
        assertThat(bookId1.toString()).isNotEqualTo(bookId2.toString())
    }

    @Nested inner class `when creating IDs from String` {

        @Test fun `UUID conform Strings are accepted`() {
            BookId.from("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")
        }

        @Test fun `non UUID conform Strings will throw an exception`() {
            assertThrows(BookId.NotAnUuidException::class.java, {
                BookId.from("not a uuid")
            })
        }

    }

}