package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.UnitTest

@UnitTest
internal class TitleTest : ValueTypeContract<Title>() {

    override fun instanceExampleOne() = Title("Title #1")
    override fun instanceExampleTwo() = Title("Title #2")

    @Test fun `toString() returns Title's value as a String`() {
        val title = Title("My Title")
        assertThat(title.toString()).isEqualTo("My Title")
    }

}