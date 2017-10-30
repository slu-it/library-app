package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class TitleTest : ValueTypeContract<Title, String>() {

    override fun getValueExample() = "Title #1"
    override fun getAnotherValueExample() = "Title #2"
    override fun createNewInstance(value: String) = Title(value)

    @Test fun `toString() returns Title's value as a String`() {
        val title = Title("My Title")
        assertThat(title.toString()).isEqualTo("My Title")
    }

}