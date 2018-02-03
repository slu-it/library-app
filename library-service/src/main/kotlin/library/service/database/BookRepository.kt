package library.service.database

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface BookRepository : MongoRepository<BookDocument, UUID> {
    fun countByBorrowedNotNull(): Long
}