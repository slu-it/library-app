export const MOCK_BOOK_LIST = {
  _embedded: {
    books: [ {
      isbn: '9780132350882',
      title: 'Clean Code: A Handbook of Agile Software Craftsmanship',
      _links: {
        self: {
          href: 'http://localhost:8080/api/books/ebce57ac-bcab-4997-ac0c-ddc97e9dedb2'
        },
        borrow: {
          href: 'http://localhost:8080/api/books/ebce57ac-bcab-4997-ac0c-ddc97e9dedb2/borrow'
        }
      }
    }, {
      isbn: '9780132350882',
      title: 'Clean Code: A Handbook of Agile Software Craftsmanship',
      borrowed: {
        by: 'slu',
        on: '2017-08-21T12:34:56.789Z'
      },
      _links: {
        self: {
          href: 'http://localhost:8080/api/books/7e61e2dd-3f5f-49d7-a5d9-8af0df146374'
        },
        return: {
          href: 'http://localhost:8080/api/books/7e61e2dd-3f5f-49d7-a5d9-8af0df146374/return'
        }
      }
    } ]
  },
  _links: {
    self: {
      href: 'http://localhost:8080/api/books'
    }
  }
}
