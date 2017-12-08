package library.service.security.annotations

import library.service.security.Authorizations
import org.springframework.security.access.prepost.PreAuthorize


@Retention
@PreAuthorize(Authorizations.IS_CURATOR_EXPRESSION)
annotation class CanOnlyBeExecutedByCurators