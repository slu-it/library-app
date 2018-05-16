package library.service.business.books

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class BookIdGeneratorTest {

    val dataStore: BookDataStore = mock()
    val cut = BookIdGenerator(dataStore)

    @Test fun `book IDs can be generated`() {
        given { dataStore.existsById(any()) } willReturn { false }
        val bookId = cut.generate()
        assertThat(bookId).isNotNull()
    }

    @Test fun `id generation is retried in case the generated id already exists`() {
        given { dataStore.existsById(any()) }
                .willReturn(true)
                .willReturn(true)
                .willReturn(false)

        cut.generate()

        verify(dataStore, times(3)).existsById(any())
    }

}