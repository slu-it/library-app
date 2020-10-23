package library.service.security

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(UserSettings::class)
class SecurityCommons(
    private val userSettings: UserSettings
) {
    fun <T : Any> executeOperationAsCurator(operation: () -> T): T {
        val originalContext = SecurityContextHolder.getContext()
        try {
            val authentication =
                UsernamePasswordAuthenticationToken(
                    userSettings.curator.username, userSettings.curator.password,
                    listOf(SimpleGrantedAuthority(Authorizations.CURATOR_ROLE))
                )
            SecurityContextHolder.setContext(SecurityContextImpl(authentication))
            return operation()
        } finally {
            if (originalContext == null) {
                SecurityContextHolder.clearContext()
            } else {
                SecurityContextHolder.setContext(originalContext)
            }
        }
    }

    fun <T : Any> executeOperationAsUser(operation: () -> T): T {
        val originalContext = SecurityContextHolder.getContext()
        try {
            val authentication =
                UsernamePasswordAuthenticationToken(
                    userSettings.user.username, userSettings.user.password,
                    listOf(SimpleGrantedAuthority(Authorizations.USER_ROLE))
                )
            SecurityContextHolder.setContext(SecurityContextImpl(authentication))
            return operation()
        } finally {
            if (originalContext == null) {
                SecurityContextHolder.clearContext()
            } else {
                SecurityContextHolder.setContext(originalContext)
            }
        }
    }
}