package library.service.security.annotations

import library.service.security.Authorizations
import org.springframework.security.access.prepost.PreAuthorize

/**
 * If security is enabled methods annotated with this annotation will not be
 * executable unless the caller is an authenticated USER. If the caller is not
 * an authenticated USER an `AccessDeniedException` will be thrown.
 */
@Retention
@PreAuthorize(Authorizations.IS_USER_EXPRESSION)
annotation class CanBeExecutedByAnyUser