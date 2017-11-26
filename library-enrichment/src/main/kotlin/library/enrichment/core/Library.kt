package library.enrichment.core

interface Library {
    fun updateBookData(bookId: String, updateData: BookData)
}