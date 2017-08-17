package library.service.persistence.books

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface BookRepository : MongoRepository<BookDocument, UUID>