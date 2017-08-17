package library.service.business.books.exceptions

import library.service.business.exceptions.NotFoundException
import java.util.*

class BookNotFoundException(id: UUID)
    : NotFoundException("The book with ID: $id does not exist!")