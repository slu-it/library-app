package library.service.business.books.domain.states

import contracts.CompositeTypeContract
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Borrower
import utils.clockWithFixedTime
import java.time.OffsetDateTime.now

internal class BorrowedTest : CompositeTypeContract<Borrowed>() {

    val fixedClock = clockWithFixedTime("2017-10-30T12:34:56.789Z")

    override fun createExampleInstance() = Borrowed(Borrower("Someone"), now(fixedClock))
    override fun createOtherExampleInstances() = listOf(
            Borrowed(Borrower("Someone"), now(fixedClock).minusDays(1)),
            Borrowed(Borrower("Someone Else"), now(fixedClock)),
            Borrowed(Borrower("Someone Else"), now(fixedClock).minusDays(1))
    )

}