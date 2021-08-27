package contracts.scc.amqp

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

listOf(
    contract {

        name = "kESMC"
        label = "Book Created Event"

        input {
            triggeredBy = "publishBookCreatedEvent()"
        }

        outputMessage {
            sentTo = sentTo("book-events")
            body = body(
                """ 
            { 
              "type":"book-added",
              "id":"88b88f1a-a76c-4d9b-93a1-d256773fed88",
              "timestamp":"2021-07-02T12:34:56Z",
              "bookId":"ebd332fe-86c9-443e-ae5e-3202d4e9af73",
              "isbn":"9780132350884",
              "title":"Clean Code: A Handbook of Agile Software Craftsmanship"
            } """
            )
            headers {
                header("amqp_receivedRoutingKey", "book-added")
                messagingContentType = "application/json"//APPLICATION_JSON
            }
        }
    },

    contract {

        name = "kESMC2"
        label = "Book Created Event 2"

        input {
            triggeredBy = "publishBookCreatedEvent()"
        }

        outputMessage {
            sentTo = sentTo("book-events")
            body = body(
                """ 
            { 
              "type":"book-added",
              "id":"88b88f1a-a76c-4d9b-93a1-d256773fed88",
              "timestamp":"2021-07-02T12:34:56Z",
              "bookId":"ebd332fe-86c9-443e-ae5e-3202d4e9af73",
              "isbn":"9780132350884",
              "title":"Clean Code: A Handbook of Agile Software Craftsmanship"
            } """
            )
            headers {
                header("amqp_receivedRoutingKey", "book-added")
                messagingContentType = "application/json"//APPLICATION_JSON
            }
        }
    }
)