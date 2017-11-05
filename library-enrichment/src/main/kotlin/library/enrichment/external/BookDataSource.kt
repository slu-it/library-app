package library.enrichment.external

interface BookDataSource {
    fun getBookData(isbn: String): BookData?
}