package library.service.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import utils.classification.UnitTest

@UnitTest
internal class UserContextTest {

    val userAuthority = SimpleGrantedAuthority("ROLE_CURATOR")
    val curatorAuthority = SimpleGrantedAuthority("ROLE_CURATOR")

    val cut = UserContext()

    @Nested inner class `user is considered a curator` {

        @Test fun `if there is no security context`() {
            SecurityContextHolder.clearContext()
            assertThat(cut.isCurator()).isTrue()
        }

        @Test fun `if the security context is empty`() {
            SecurityContextHolder.setContext(SecurityContextImpl())
            assertThat(cut.isCurator()).isTrue()
        }

        @Test fun `if the user has role CURATOR`() {
            SecurityContextHolder.setContext(createSecurityContext(userAuthority, curatorAuthority))
            assertThat(cut.isCurator()).isTrue()
        }

    }

    @Test fun `user is not considered a curator if CURATOR role is missing`() {
        SecurityContextHolder.setContext(createSecurityContext(userAuthority))
        assertThat(cut.isCurator()).isTrue()
    }

    private fun createSecurityContext(vararg authorities: GrantedAuthority): SecurityContextImpl {
        val authentication = UsernamePasswordAuthenticationToken("user", "user", listOf(*authorities))
        return SecurityContextImpl(authentication)
    }

}