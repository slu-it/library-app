import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { BookListResource } from '../model/book-list-resource';
import { Observable } from 'rxjs/Observable';
import { BookService } from '../service/book.service';
import { Injectable } from '@angular/core';

@Injectable()
export class LoadBooksResolver implements Resolve<BookListResource> {

  constructor(private _bookService: BookService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
      Observable<BookListResource> | Promise<BookListResource> | BookListResource {
    return this._bookService.findAllBooks();
  }

}
