package library.enrichment.core

/**
 * A [BookDataSource] can be queried for [BookData]. This data can then be used
 * to update a book in the [Library].
 *
 * A data source is not required to return a full [BookData] instance. Properties
 * which can be found in one data source might not be found in another. It is the
 * using component's task to decide which data to use and which to discard.
 *
 * @see BookData
 * @see Library
 */
interface BookDataSource {

    /**
     * Returns all available [BookData] this [BookDataSource] has about a book
     * with the given `ISBN`.
     *
     * This method should never throw an exception! Instead it should return
     * a `<null>` [BookData] instance and log whatever went wrong.
     *
     * @see BookData
     * @see BookDataSource
     */
    fun getBookData(isbn: String): BookData?

}