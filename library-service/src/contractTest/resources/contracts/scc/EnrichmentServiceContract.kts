package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract
import org.springframework.hateoas.MediaTypes.HAL_JSON

contract {
    description = "update authors of a book"

    request {
        method = PUT
        urlPath = path("/api/books/3c15641e-2598-41f5-9097-b37e2d768be5/authors")
        headers {
            header("X-Correlation-Id", "5d59f7da-f52f-46df-85c5-2d97b3b42aad")
            contentType = APPLICATION_JSON
        }
        body = body(
            """
            {
              "authors": [
                "J.R.R. Tolkien",
                "Jim Butcher"
              ]
            }
            """
        )
    }

    response {
        status = OK
        headers {
            header("X-Correlation-Id", "5d59f7da-f52f-46df-85c5-2d97b3b42aad")
            contentType = HAL_JSON
        }
    }
}
