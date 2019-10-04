package library.service.api.index

import library.service.api.books.BooksController
import library.service.security.UserContext
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class IndexController(
        private val currentUser: UserContext
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(): VoidRepresentationModel = VoidRepresentationModel().apply {
        add(linkTo(methodOn(IndexController::class.java).get()).withSelfRel())
        add(linkTo(methodOn(BooksController::class.java).getBooks()).withRel("getBooks"))
        if (currentUser.isCurator()) {
            add(linkTo(methodOn(BooksController::class.java).getBooks()).withRel("addBook"))
        }
    }

    class VoidRepresentationModel : RepresentationModel<VoidRepresentationModel>()

}