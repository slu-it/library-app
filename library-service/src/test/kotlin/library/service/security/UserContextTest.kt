package library.service.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder.clearContext
import org.springframework.security.core.context.SecurityContextHolder.setContext
import org.springframework.security.core.context.SecurityContextImpl
import utils.classification.UnitTest

@UnitTest
internal class UserContextTest {

    val userRole = SimpleGrantedAuthority(Authorizations.USER_ROLE)
    val curatorRole = SimpleGrantedAuthority(Authorizations.CURATOR_ROLE)

    val cut = UserContext()

    @BeforeEach @AfterEach
    fun clearStaticSecurityContext() = clearContext()

    @Nested inner class `user is considered a curator` {

        @Test fun `if there is no security context`() {
            assertThat(cut.isCurator()).isTrue()
        }

        @Test fun `if the security context is empty`() {
            setContext(securityContext())
            assertThat(cut.isCurator()).isTrue()
        }

        @Test fun `if the user has role CURATOR`() {
            setContext(securityContext(withAuthorizations(userRole, curatorRole)))
            assertThat(cut.isCurator()).isTrue()
        }

    }

    @Test fun `user is not considered a curator if CURATOR role is missing`() {
        setContext(securityContext(withAuthorizations(userRole)))
        assertThat(cut.isCurator()).isFalse()
    }

    fun securityContext() = SecurityContextImpl()
    fun securityContext(authentication: Authentication) = SecurityContextImpl(authentication)
    fun withAuthorizations(vararg authorities: GrantedAuthority) =
            UsernamePasswordAuthenticationToken("username", "password", listOf(*authorities))

}