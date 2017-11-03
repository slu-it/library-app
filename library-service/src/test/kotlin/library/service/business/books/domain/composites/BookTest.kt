package library.service.business.books.domain.composites

import contracts.CompositeTypeContract
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import utils.classification.UnitTest

@UnitTest
internal class BookTest : CompositeTypeContract<Book>() {

    override fun createExampleInstance() = Book(Isbn13("1234567890123"), Title("Book #1"))
    override fun createOtherExampleInstances() = listOf(
            Book(Isbn13("1234567890123"), Title("Book #2")),
            Book(Isbn13("0123456789012"), Title("Book #1")),
            Book(Isbn13("0123456789012"), Title("Book #2"))
    )

}