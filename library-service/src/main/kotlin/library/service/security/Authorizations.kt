package library.service.security

object Authorizations {
    const val IS_CURATOR = "hasRole('ROLE_${Roles.CURATOR}')"
    const val IS_USER = "hasRole('ROLE_${Roles.USER}')"
}