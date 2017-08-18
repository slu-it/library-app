package library.service.business.books.domain.states

import library.service.business.books.domain.types.Borrower
import java.time.OffsetDateTime

data class Borrowed(
        val by: Borrower,
        val on: OffsetDateTime
)