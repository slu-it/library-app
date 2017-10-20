package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.classification.UnitTest

@UnitTest
internal class BorrowerTest : ValueTypeContract<Borrower>() {

    override fun instanceExampleOne() = Borrower("Rob Stark")
    override fun instanceExampleTwo() = Borrower("Ned Stark")

    @Test fun `toString() returns Borrower's value as a String`() {
        val borrower = Borrower("slu")
        assertThat(borrower.toString()).isEqualTo("slu")
    }

}