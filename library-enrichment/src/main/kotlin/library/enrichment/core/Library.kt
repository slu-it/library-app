package library.enrichment.core

/**
 * [Library] implementations offer methods for interacting with the library of
 * books on record.
 *
 * @see updateAuthors
 * @see updateNumberOfPages
 */
interface Library {

    /**
     * Updates the authors of the book - with the given `bookId` - to the list
     * of `authors`.
     *
     * The list must never be empty! An empty author list is forbidden by the
     * actual library and would therefore lead to an exception somewhere down
     * the line.
     *
     * @param bookId the unique ID of the book inside the library
     * @param authors the list of authors to set - may not be empty
     */
    fun updateAuthors(bookId: String, authors: List<String>)

    /**
     * Updates the number of pages of the book - with the given `bookId` - to
     * the `numberOfPages` value.
     *
     * The value must be greater or equal to 1! Any lower value is forbidden by
     * the actual library and would therefore lead to an exception somewhere down
     * the line.
     *
     * @param bookId the unique ID of the book inside the library
     * @param numberOfPages the number of pages to set - may not be less than 1
     */
    fun updateNumberOfPages(bookId: String, numberOfPages: Int)

}