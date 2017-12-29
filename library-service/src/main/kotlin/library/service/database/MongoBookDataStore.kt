package library.service.database

import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.BookId
import library.service.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service

@Service
@LogMethodEntryAndExit
class MongoBookDataStore(
        private val repository: BookRepository,
        private val bookRecordToDocumentMapper: Mapper<BookRecord, BookDocument>,
        private val bookDocumentToRecordMapper: Mapper<BookDocument, BookRecord>
) : BookDataStore {

    override fun createOrUpdate(bookRecord: BookRecord): BookRecord {
        val document = bookRecordToDocumentMapper.map(bookRecord)
        val updatedDocument = repository.save(document)
        return bookDocumentToRecordMapper.map(updatedDocument)
    }

    override fun delete(bookRecord: BookRecord) {
        repository.deleteById(bookRecord.id.toUuid())
    }

    override fun findById(id: BookId): BookRecord? {
        return repository.findById(id.toUuid())
                .map(bookDocumentToRecordMapper::map)
                .orElse(null)
    }

    override fun findAll(): List<BookRecord> {
        return repository.findAll()
                .map(bookDocumentToRecordMapper::map)
    }

    override fun existsById(bookId: BookId): Boolean {
        return repository.existsById(bookId.toUuid())
    }

}