package library.service.api.index

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.willReturn
import library.service.security.UserContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest


@UnitTest
internal class IndexControllerTest {

    val currentUser: UserContext = mock()
    val cut = IndexController(currentUser)

    @Test fun `self link is generated`() {
        assertThat(cut.get().getLink("self")).isNotNull()
    }

    @Test fun `getBooks link is generated`() {
        assertThat(cut.get().getLink("getBooks")).isNotNull()
    }

    @Nested inner class `addBook link` {

        @Test fun `is generated for curator users`() {
            given { currentUser.isCurator() } willReturn { true }
            assertThat(cut.get().getLink("addBook")).isNotNull()
        }

        @Test fun `is not generated for non-current users`() {
            given { currentUser.isCurator() } willReturn { false }
            assertThat(cut.get().getLink("addBook")).isNull()
        }

    }

}