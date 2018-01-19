package library.enrichment.core

interface Library {
    fun updateAuthors(bookId: String, authors: List<String>)
    fun updateNumberOfPages(bookId: String, numberOfPages: Int)
}