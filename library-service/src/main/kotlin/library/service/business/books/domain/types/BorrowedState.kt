package library.service.business.books.domain.types

import java.time.OffsetDateTime

data class BorrowedState(
        val by: Borrower,
        val on: OffsetDateTime
)