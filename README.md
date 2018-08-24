[![Build Status](https://travis-ci.org/nt-ca-aqe/library-app.svg?branch=master)](https://travis-ci.org/nt-ca-aqe/library-app)

# Library Application

This is a showcase application. It aims to demonstrate several software engineering
practices and a lot of current technology. The application itself is book management
system which allows for:

- adding of new books
- listing of books
- borrowing of books
- returning of borrowed books

The application is divided into several modules:

- The `library-service` is the main backend application. It manages the data,
dispatches domain events and manages consistency.
- The `library-enrichment` is a background service which reacts to `book-added`
domain events by looking up additional information about newly added books. If
it finds any, it will update the book record via the `library-service`.
- The `library-integration-slack` is a  service which reacts to `book-added`
domain events by posting them to a configured slack channel.
- The `library-ui` is a client side frontend for the `library-service`. It allows
for the management of the library as well as the borrowing and returning
of books by customers.

## Technology Showcases

- Kotlin (`library-service` and `library-enrichment`)
- Spring Boot 2 (`library-service` and `library-enrichment`)
- JUnit 5 with custom extensions (`library-service` and `library-enrichment`)
- MongoDB with Spring Data (`library-service`)
- AMQP with RabbitMQ (`library-service` and `library-enrichment`)
- Hypermedia APIs with Spring HATEOAS (`library-service`)
- Test-driven API documentation with Spring REST Docs (`library-service`)
- Documentation generation with Asciidoctor (`library-service`)
- Declarative REST Clients with Feign (`library-enrichment`)
- Contract Testing with PACT (`library-service` and `library-enrichment`)

## Software Engineering Practices

- Modular Application Design
- Object Oriented Design
- Hexagonal Architectures
- Clean Code
- Testable Architecture / Design
- Test Automation
 - Unit, Integration and Acceptance Tests
 - Testing application slices
