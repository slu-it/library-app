import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BookListResource } from '../model/book-list-resource';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BookResource } from '../model/book-resource';
import { CreateBookResource } from '../model/create-book-resource';
import { BorrowBookResource } from '../model/borrow-book-resource';

@Injectable()
export class BookService {

  constructor(private _httpClient: HttpClient) {}

  /**
   * Find all available books
   * (Currently does not yet call a backend service!)
   * @returns {Observable<BookListResource>}
   */
  public findAllBooks(): Observable<BookListResource> {
    return this._httpClient
      .get(environment.libraryService + 'books')
      .map(data => Object.assign(new BookListResource(), data));
  }

  public createBook(createBookResource: CreateBookResource): Observable<BookResource> {
    return this._httpClient
      .post(environment.libraryService + 'books', createBookResource)
      .map(data => Object.assign(new BookResource(), data));
  }

  public deleteBook(bookResource: BookResource): Observable<void> {
    return this._httpClient
      .delete(bookResource._links.self.href)
      .map(data => null);
  }

  public borrowBook(bookResource: BookResource, borrowBookResource: BorrowBookResource): Observable<BookResource> {
    return this._httpClient
      .post(bookResource._links.borrow.href, borrowBookResource)
      .map(data => Object.assign(new BookResource(), data));
  }

  public returnBook(bookResource: BookResource): Observable<BookResource> {
    return this._httpClient
      .post(bookResource._links.return.href, {})
      .map(data => Object.assign(new BookResource(), data));
  }
}
