package library.service.business.books.exceptions

import library.service.business.exceptions.NotPossibleException
import java.util.*

class BookAlreadyReturnedException(id: UUID)
    : NotPossibleException("The book with ID: $id was already returned!")