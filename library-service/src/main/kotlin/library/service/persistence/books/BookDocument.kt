package library.service.persistence.books

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "books")
data class BookDocument(

        @Id
        var id: UUID? = null,

        var isbn: String? = null,
        var title: String? = null,
        var borrowed: Borrowed? = null

) {

    class Borrowed(
            var by: String? = null,
            var on: String? = null
    )

}