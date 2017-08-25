package library.service.business.books.domain.types

import contracts.ValueTypeContract
import utils.UnitTest

@UnitTest
internal class BookTest : ValueTypeContract<Book>() {

    override fun newExampleInstanceOne() = Book(Isbn13("1234567890123"), Title("Book #1"))
    override fun newExampleInstanceTwo() = Book(Isbn13("0123456789012"), Title("Book #2"))

}