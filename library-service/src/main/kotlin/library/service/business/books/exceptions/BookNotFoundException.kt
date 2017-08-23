package library.service.business.books.exceptions

import library.service.business.books.domain.types.BookId
import library.service.business.exceptions.NotFoundException

class BookNotFoundException(id: BookId)
    : NotFoundException("The book with ID: $id does not exist!")