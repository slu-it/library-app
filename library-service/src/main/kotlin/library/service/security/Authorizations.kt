package library.service.security

object Authorizations {

    const val USER_ROLE = "ROLE_${Roles.USER}"
    const val CURATOR_ROLE = "ROLE_${Roles.CURATOR}"

    const val IS_USER_EXPRESSION = "hasRole('$USER_ROLE')"
    const val IS_CURATOR_EXPRESSION = "hasRole('$CURATOR_ROLE')"

}