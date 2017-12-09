package library.service.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContext {

    fun isCurator() = currentUserHasRole(Authorizations.CURATOR_ROLE)

    private fun currentUserHasRole(role: String)= SecurityContextHolder.getContext()
            ?.authentication
            ?.authorities
            ?.any { it.authority == role }
            ?: true

}