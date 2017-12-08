package library.service.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContext {

    fun isCurator() = currentUserHasRole(Authorizations.CURATOR_ROLE)

    private fun currentUserHasRole(role: String): Boolean {
        val authentication = SecurityContextHolder.getContext()?.authentication
        if (authentication != null) {
            return authentication.authorities.any { it.authority == role }
        }
        return true
    }

}