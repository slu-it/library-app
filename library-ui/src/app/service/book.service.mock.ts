import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BookListResource } from '../model/book-list-resource';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import { BookResource } from '../model/book-resource';
import { CreateBookResource } from '../model/create-book-resource';
import { BorrowBookResource } from '../model/borrow-book-resource';
import { MOCK_BOOK_LIST } from '../model/book-list.mock';
import { Observer } from 'rxjs/Observer';
import { MOCK_BOOK } from '../model/book-mock';

@Injectable()
export class BookServiceMock {

  constructor() {}

  /**
   * Find all available books
   * (Currently does not yet call a backend service!)
   * @returns {Observable<BookListResource>}
   */
  public findAllBooks(): Observable<BookListResource> {
    return Observable.create((observer: Observer<BookListResource>) => {
      observer.next( Object.assign(new BookListResource(),
        MOCK_BOOK_LIST
      ));
      observer.complete();
    })
  }

  public createBook(createBookResource: CreateBookResource): Observable<BookResource> {
    return Observable.create((observer: Observer<BookResource>) => {
      observer.next( Object.assign(new BookResource(),
        MOCK_BOOK
      ));
      observer.complete();
    })
  }

  public deleteBook(bookResource: BookResource): Observable<void> {
    return Observable.of(null);
  }

  public borrowBook(bookResource: BookResource, borrowBookResource: BorrowBookResource): Observable<BookResource> {
    return Observable.create((observer: Observer<BookResource>) => {
      observer.next( Object.assign(new BookResource(),
        MOCK_BOOK
      ));
      observer.complete();
    })
  }

  public returnBook(bookResource: BookResource): Observable<BookResource> {
    return Observable.create((observer: Observer<BookResource>) => {
      observer.next( Object.assign(new BookResource(),
        MOCK_BOOK
      ));
      observer.complete();
    })
  }
}
