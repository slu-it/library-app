package library.service.api.index

import library.service.api.books.BooksController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class IndexController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(): ResourceSupport {
        return ResourceSupport().apply {
            add(linkTo(methodOn(IndexController::class.java).get()).withSelfRel())
            add(linkTo(methodOn(BooksController::class.java).getBooks()).withRel("getBooks"))
            add(linkTo(methodOn(BooksController::class.java).getBooks()).withRel("addBook"))
        }
    }

}