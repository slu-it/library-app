package library.service.persistence.books

import library.service.business.books.BookDataStore
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.BookId
import library.service.common.Mapper
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service

@Service
@LogMethodEntryAndExit
class MongoBookDataStore(
        private val repository: BookRepository,
        private val bookToDocumentMapper: Mapper<Book, BookDocument>,
        private val bookRecordToDocumentMapper: Mapper<BookRecord, BookDocument>,
        private val bookDocumentToRecordMapper: Mapper<BookDocument, BookRecord>
) : BookDataStore {

    override fun create(book: Book): BookRecord {
        val document = bookToDocumentMapper.map(book)
        val createdDocument = repository.save(document)
        return bookDocumentToRecordMapper.map(createdDocument)
    }

    override fun update(bookRecord: BookRecord): BookRecord {
        val document = bookRecordToDocumentMapper.map(bookRecord)
        val updatedDocument = repository.save(document)
        return bookDocumentToRecordMapper.map(updatedDocument)
    }

    override fun delete(bookRecord: BookRecord) {
        val bookId = bookRecord.id.toUuid()
        repository.deleteById(bookId)
    }

    override fun findById(id: BookId): BookRecord? {
        val bookId = id.toUuid()
        return repository.findById(bookId)
                .map(bookDocumentToRecordMapper::map)
                .orElse(null)
    }

    override fun findAll(): List<BookRecord> {
        return repository.findAll()
                .map(bookDocumentToRecordMapper::map)
    }

}