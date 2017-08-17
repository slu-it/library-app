package library.service.api.books

import org.springframework.hateoas.RelProvider
import org.springframework.stereotype.Component

@Component
class BooksRelProvider : RelProvider {

    override fun supports(delimiter: Class<*>?) = BookResource::class.java == delimiter

    override fun getItemResourceRelFor(type: Class<*>?) = "book"
    override fun getCollectionResourceRelFor(type: Class<*>?) = "books"

}