import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BookResource } from "../model/book-resource";
import { BookService } from '../service/book.service';
import { HttpErrorResponse } from '@angular/common/http';
import { BorrowBookResource } from '../model/borrow-book-resource';

@Component({
  selector: 'lib-book-list-item',
  templateUrl: './book-list-item.component.html',
  styleUrls: ['./book-list-item.component.css']
})
export class BookListItemComponent implements OnInit {

  @Input()
  public book: BookResource;

  @Input()
  public index?: number = 0;

  @Output()
  public onUpdate = new EventEmitter<boolean>();

  public borrower: Borrower;

  public borrowActive: boolean = false;

  public returnActive: boolean = false;

  constructor(private _bookService: BookService) { }

  ngOnInit() {
    this.borrower = new Borrower('');
  }

  onBorrow() {
    this.borrowActive = !this.borrowActive;
  }

  onReturn() {
    this.returnActive = !this.returnActive;
  }

  borrowBook() {
    const borrowBook: BorrowBookResource = new BorrowBookResource(this.borrower.name);
    this._bookService.borrowBook(this.book, borrowBook).subscribe(
      data => { this.borrowActive = false; this.updateList() },
      (err: HttpErrorResponse) => console.log('Error when borrowing book ' + err.message)
    )
  }

  returnBook() {
    this._bookService.returnBook(this.book).subscribe(
      data => { this.returnActive = false; this.updateList() },
      (err: HttpErrorResponse) => console.log('Error when returning book ' + err.message)
    )
  }

  onDelete() {
    this._bookService.deleteBook(this.book).subscribe(
      data => this.updateList(),
      (err: HttpErrorResponse) => console.log('Error when deleting book ' + err.message)
    )
  }

  updateList() {
    this.onUpdate.emit(true);
  }
}

export class Borrower {
  constructor(public name: string) {}
}
