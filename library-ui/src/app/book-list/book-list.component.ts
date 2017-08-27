import { Component, OnInit } from '@angular/core';
import {BookService} from "../service/book.service";
import {BookResource} from "../model/book-resource";
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResource } from '../model/error-resource';
import { ErrorMessage } from '../shared/error-message';

@Component({
  selector: 'lib-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {

  public books: BookResource[];

  public filter: BookFilter;

  public error: ErrorMessage;

  constructor(private _bookService: BookService) { }

  ngOnInit() {
    this.filter = new BookFilter('','');
    this.error = new ErrorMessage(false, '');
    this.loadList();
  }

  onFilter() {
    this.loadList();
  }

  onRefresh() {
    this.filter = new BookFilter('', '');
    this.loadList();
  }

  refreshList(update: boolean) {
    this.loadList();
  }

  private loadList() {
    this._bookService.findAllBooks().subscribe(
      bl => {
        if (bl._embedded) {
          if (this.filter.isTitleFilterActive() && this.filter.isIsbnFilterActive()) {
            this.books = bl._embedded.books.filter(
              b => b.title.toUpperCase().includes(this.filter.title.toUpperCase())
                && b.isbn.includes(this.filter.isbn));
          } else if (this.filter.isTitleFilterActive()) {
            this.books = bl._embedded.books.filter(
              b => b.title.toUpperCase().includes(this.filter.title.toUpperCase()));
          } else if (this.filter.isIsbnFilterActive()) {
            this.books = bl._embedded.books.filter(
              b => b.isbn.includes(this.filter.isbn));
          } else {
            this.books = bl._embedded.books
          }
        } else {
          this.books = [];
        }
      },
      (err: HttpErrorResponse) => {
        console.log(`Backend returned code ${err.status}, body was: ${err.error}`);
        const errorResource: ErrorResource = Object.assign(new ErrorResource(null, '', '', ''), err.error);
        this.error = new ErrorMessage(true, errorResource.details);
      }
    );
  }
}

class BookFilter {
  constructor(public title: string, public isbn: string) {}

  isTitleFilterActive(): boolean {
    return this.title && this.title.length > 0;
  }

  isIsbnFilterActive(): boolean {
    return this.isbn && this.isbn.length > 0;
  }

}
