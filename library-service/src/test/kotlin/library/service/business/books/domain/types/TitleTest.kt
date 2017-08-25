package library.service.business.books.domain.types

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.UnitTest

@UnitTest
internal class TitleTest {

    @Test fun `toString() returns Title's value as a String`() {
        val title = Title("My Title")
        assertThat(title.toString()).isEqualTo("My Title")
    }

}