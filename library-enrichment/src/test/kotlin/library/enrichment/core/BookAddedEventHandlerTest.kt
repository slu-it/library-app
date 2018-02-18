package library.enrichment.core

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.UnitTest


@UnitTest
internal class BookAddedEventHandlerTest {

    val event = BookAddedEvent(
            id = "5d1f6545-0c48-4826-bbea-d40a0163e479",
            bookId = "7600707e-bf21-465c-91a0-56358e586352",
            isbn = "1234567890123"
    )

    val dataSource1: BookDataSource = mock {
        on { toString() } doReturn "dataSource1"
    }
    val dataSource2: BookDataSource = mock {
        on { toString() } doReturn "dataSource2"
    }
    val library: Library = mock()

    @Nested inner class `single data source` {

        val cut = BookAddedEventHandler(listOf(dataSource1), library)

        @Test fun `null result will stop processing of event`() {
            cut.handle(event)
            verifyZeroInteractions(library)
        }

        @Test fun `empty result will be ignored`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData()
            }
            cut.handle(event)
            verifyZeroInteractions(library)
        }

        @Test fun `result with one author will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo"))
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with multiple authors will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo", "bar"))
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo", "bar"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with number of pages higher than 0 will trigger update in library`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 1)
            }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 1)
            verifyNoMoreInteractions(library)
        }

        @Test fun `result with number of pages of 0 will be ignored`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 0)
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

        val cut = BookAddedEventHandler(listOf(dataSource1, dataSource2), library)

        @Test fun `the first datasource with authors wins - all return values`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo"))
            }
            given { dataSource2.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("bar"))
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("foo"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with authors wins - only one returns value`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = emptyList())
            }
            given { dataSource2.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("bar"))
            }
            cut.handle(event)
            verify(library).updateAuthors(event.bookId, listOf("bar"))
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with any number of pages wins - all return values`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 1)
            }
            given { dataSource2.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 2)
            }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 1)
            verifyNoMoreInteractions(library)
        }

        @Test fun `the first datasource with any number of pages wins - only one returns value`() {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = null)
            }
            given { dataSource2.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 2)
            }
            cut.handle(event)
            verify(library).updateNumberOfPages(event.bookId, 2)
            verifyNoMoreInteractions(library)
        }

    }

    @Nested inner class `handling of events is logged` {

        val cut = BookAddedEventHandler(listOf(dataSource1, dataSource2), library)

        @RecordLoggers(BookAddedEventHandler::class)
        @Test fun `when no data could be gathered`(log: LogRecord) {
            cut.handle(event)
            assertThat(log.messages).containsExactly(
                    "processing book added event: $event",
                    "looking up book data using [dataSource1]",
                    "looking up book data using [dataSource2]",
                    "could not find any data sets for ISBN [1234567890123]"
            )
        }

        @RecordLoggers(BookAddedEventHandler::class)
        @Test fun `when data could be gathered from one source`(log: LogRecord) {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo", "bar"), numberOfPages = 128)
            }
            cut.handle(event)
            assertThat(log.messages).containsExactly(
                    "processing book added event: $event",
                    "looking up book data using [dataSource1]",
                    "looking up book data using [dataSource2]",
                    "found 1 data set(s) for ISBN [${event.isbn}]",
                    "chose [foo, bar] as the best author(s), updating book record ...",
                    "chose [128] as the best number of pages, updating book record ..."
            )
        }

        @RecordLoggers(BookAddedEventHandler::class)
        @Test fun `when data could be gathered from multiple source`(log: LogRecord) {
            given { dataSource1.getBookData(event.isbn) } willReturn {
                BookData(authors = listOf("foo", "bar"))
            }
            given { dataSource2.getBookData(event.isbn) } willReturn {
                BookData(numberOfPages = 256)
            }
            cut.handle(event)
            assertThat(log.messages).containsExactly(
                    "processing book added event: $event",
                    "looking up book data using [dataSource1]",
                    "looking up book data using [dataSource2]",
                    "found 2 data set(s) for ISBN [${event.isbn}]",
                    "chose [foo, bar] as the best author(s), updating book record ...",
                    "chose [256] as the best number of pages, updating book record ..."
            )
        }

    }

}