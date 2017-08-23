package library.service.business.books.exceptions

import library.service.business.books.domain.types.BookId
import library.service.business.exceptions.NotPossibleException

class BookAlreadyReturnedException(id: BookId)
    : NotPossibleException("The book with ID: $id was already returned!")