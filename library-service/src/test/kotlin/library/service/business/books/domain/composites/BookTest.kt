package library.service.business.books.domain.composites

import contracts.CompositeTypeContract
import utils.Books
import utils.classification.UnitTest

@UnitTest
internal class BookTest : CompositeTypeContract<Book>() {

    override fun createExampleInstance() = Books.THE_MARTIAN
    override fun createOtherExampleInstances() = listOf(
            Books.THE_LORD_OF_THE_RINGS_1,
            Books.THE_LORD_OF_THE_RINGS_2,
            Books.THE_LORD_OF_THE_RINGS_3
    )

}