package library.enrichment.core

interface BookDataSource {
    fun getBookData(isbn: String): BookData?
}