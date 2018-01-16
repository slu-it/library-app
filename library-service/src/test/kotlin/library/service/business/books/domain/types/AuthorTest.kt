package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class AuthorTest : ValueTypeContract<Author, String>() {

    override fun getValueExample() = "Author #1"
    override fun getAnotherValueExample() = "Author #2"
    override fun createNewInstance(value: String) = Author(value)

    @Test fun `toString() returns Author's value as a String`() {
        val author = Author("My Author")
        assertThat(author.toString()).isEqualTo("My Author")
    }

}