import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {BookListResource} from '../model/book-list-resource';
import 'rxjs/add/observable/of';
import {Observer} from "rxjs/Observer";
import {MOCK_BOOK_LIST} from "../model/book-list.mock";

@Injectable()
export class BookService {

  constructor() { }

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

}
