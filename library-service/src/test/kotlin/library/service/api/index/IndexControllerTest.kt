package library.service.api.index

import io.mockk.every
import io.mockk.mockk
import library.service.security.UserContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest


@UnitTest
internal class IndexControllerTest {

    val currentUser: UserContext = mockk()
    val cut = IndexController(currentUser)

    @BeforeEach fun setMockDefaults(){
        every { currentUser.isCurator() } returns false
    }

    @Test fun `self link is generated`() {
        assertThat(cut.get().getLink("self")).isNotNull()
    }

    @Test fun `getBooks link is generated`() {
        assertThat(cut.get().getLink("getBooks")).isNotNull()
    }

    @Nested inner class `addBook link` {

        @Test fun `is generated for curator users`() {
            every { currentUser.isCurator() } returns true
            assertThat(cut.get().getLink("addBook")).isNotNull()
        }

        @Test fun `is not generated for non-current users`() {
            every { currentUser.isCurator() } returns false
            assertThat(cut.get().getLink("addBook")).isNull()
        }

    }

}