package library.service.business.books.exceptions

import library.service.business.exceptions.NotPossibleException
import java.util.*

class BookAlreadyBorrowedException(id: UUID)
    : NotPossibleException("The book with ID: $id is already borrowed!")