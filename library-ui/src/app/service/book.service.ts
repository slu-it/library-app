import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BookListResource } from '../model/book-list-resource';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

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
      .map(data => Object.assign(new BookListResource(), data))
    /*return Observable.create((observer: Observer<BookListResource>) => {
      observer.next( Object.assign(new BookListResource(),
        MOCK_BOOK_LIST
      ));
      observer.complete();
    })*/
  }
}
