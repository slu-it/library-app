package library.service.business.books.domain.types

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.UnitTest

@UnitTest
internal class BorrowerTest {

    @Test fun `toString() returns Borrower's value as a String`() {
        val borrower = Borrower("slu")
        assertThat(borrower.toString()).isEqualTo("slu")
    }

}