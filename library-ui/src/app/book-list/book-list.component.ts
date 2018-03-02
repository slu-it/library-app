import { Component, OnInit } from '@angular/core';
import {BookService} from "../service/book.service";
import {BookResource} from "../model/book-resource";
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResource } from '../model/error-resource';
import { MatSnackBar } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { BookListResource } from '../model/book-list-resource';

@Component({
  selector: 'lib-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {

  public books: BookResource[];

  public filter: BookFilter = new BookFilter('','', false);

  private _storage = localStorage;

  constructor(private _bookService: BookService, private _route: ActivatedRoute, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    console.log('ngOnInit');
    this.restoreFilter();
    this.filterList(this._route.snapshot.data['bookList']);
  }

  onFilter() {
    this.storeFilter();
    this.loadList();
  }

  onRefresh() {
    this.filter = new BookFilter('', '', false);
    this.storeFilter();
    this.loadList();
  }

  refreshList(update: boolean) {
    this.loadList();
  }

  private storeFilter() {
    if (this.filter.available !== null) {
      this._storage.setItem('lib.filter.available', this.filter.available.toString());
    }
    if (this.filter.title) {
      this._storage.setItem('lib.filter.title', this.filter.title);
    } else {
      this._storage.removeItem('lib.filter.title');
    }
    if (this.filter.isbn) {
      this._storage.setItem('lib.filter.isbn', this.filter.isbn);
    } else {
      this._storage.removeItem('lib.filter.isbn');
    }
  }

  private restoreFilter() {
    if (this._storage.getItem('lib.filter.available') === 'true') {
      this.filter.available = true;
    } else {
      this.filter.available = false;
    }
    if (this._storage.getItem('lib.filter.title')) {
      this.filter.title = this._storage.getItem('lib.filter.title');
    }
    if (this._storage.getItem('lib.filter.isbn')) {
      this.filter.isbn = this._storage.getItem('lib.filter.isbn');
    }
  }

  private loadList() {
    this._bookService.findAllBooks().subscribe(
      bl => {
        this.filterList(bl);
        this._snackBar.open('Successfully loaded books', 'dismiss', {
          duration: 5000
        });
      },
      (err: HttpErrorResponse) => {
        console.log(`Backend returned code ${err.status}, body was: ${err.error}`);
        this._snackBar.open('Error loading books: ' + err.message, 'dismiss', {
          duration: 10000
        });
      }
    );
  }

  private filterList(bookList: BookListResource) {
      if (bookList._embedded) {
        if (this.filter.isTitleFilterActive() && this.filter.isIsbnFilterActive()) {
          this.books = bookList._embedded.books.filter(
            b => b.title.toUpperCase().includes(this.filter.title.toUpperCase())
              && b.isbn.includes(this.filter.isbn));
        } else if (this.filter.isTitleFilterActive()) {
          this.books = bookList._embedded.books.filter(
            b => b.title.toUpperCase().includes(this.filter.title.toUpperCase()));
        } else if (this.filter.isIsbnFilterActive()) {
          this.books = bookList._embedded.books.filter(
            b => b.isbn.includes(this.filter.isbn));
        } else {
          this.books = bookList._embedded.books
        }
        if (this.filter.available) {
          this.books = this.books.filter(
            b => !b.borrowed
          )
        }
      } else {
        this.books = [];
      }
    }
  }

class BookFilter {
  constructor(public title: string, public isbn: string, public available: boolean) {}

  isTitleFilterActive(): boolean {
    return this.title && this.title.length > 0;
  }

  isIsbnFilterActive(): boolean {
    return this.isbn && this.isbn.length > 0;
  }

}
