package library.enrichment.core

import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*


internal class BookEventHandlerTest {

    val event = BookAddedEvent(
            id = UUID.randomUUID().toString(),
            bookId = UUID.randomUUID().toString(),
            isbn = "1234567890123"
    )

    val dataSource1: BookDataSource = mock()
    val dataSource2: BookDataSource = mock()
    val library: Library = mock()

    @Nested inner class `single data source` {

        val cut = BookEventHandler(listOf(dataSource1), library)

        @Test fun `null result will stop processing of event`() {
            given { dataSource1.getBookData(event.isbn) } willReturn { null }
            cut.handle(event)
            verifyZeroInteractions(library)
        }

        @Test fun `empty result will be ignored`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = emptyList(), numberOfPages = null)
            }
            cut.handle(event)
            verifyZeroInteractions(library)
        }

        @Test fun `result with one author will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo"), numberOfPages = null)
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with multiple authors will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo", "bar"), numberOfPages = null)
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo", "bar"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with number of pages higher than 0 will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = emptyList(), numberOfPages = 1)
            }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 1)
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with number of pages of 0 will be ignored`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = emptyList(), numberOfPages = 0)
            }
            cut.handle(event)
            verifyZeroInteractions(library)
        }

        @Test fun `result with data will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo", "bar"), numberOfPages = 1)
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo", "bar"))
            verify(library).updateNumberOfPages(event.bookId, 1)
            verifyNoMoreInteractions(library)
        }

    }

    @Nested inner class `given multiple data sources` {

        val cut = BookEventHandler(listOf(dataSource1, dataSource2), library)

        @Test fun `the first datasource with authors wins - all return values`() {
            given { dataSource1.getBookData(event.isbn) } willReturn { BookData(authors = listOf("foo")) }
            given { dataSource2.getBookData(event.isbn) } willReturn { BookData(authors = listOf("bar")) }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with authors wins - only one returns value`() {
            given { dataSource1.getBookData(event.isbn) } willReturn { BookData(authors = emptyList()) }
            given { dataSource2.getBookData(event.isbn) } willReturn { BookData(authors = listOf("bar")) }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("bar"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with any number of pages wins - all return values`() {
            given { dataSource1.getBookData(event.isbn) } willReturn { BookData(numberOfPages = 1) }
            given { dataSource2.getBookData(event.isbn) } willReturn { BookData(numberOfPages = 2) }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 1)
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with any number of pages wins - only one returns value`() {
            given { dataSource1.getBookData(event.isbn) } willReturn { BookData(numberOfPages = null) }
            given { dataSource2.getBookData(event.isbn) } willReturn { BookData(numberOfPages = 2) }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 2)
            verifyNoMoreInteractions(library)
        }

    }
}