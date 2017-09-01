import { Component, OnInit } from '@angular/core';
import { CreateBookResource } from '../model/create-book-resource';
import { BookService } from '../service/book.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResource } from '../model/error-resource';
import { ErrorMessage } from '../shared/error-message';

@Component({
  selector: 'lib-book-create',
  templateUrl: './book-create.component.html',
  styleUrls: ['./book-create.component.css']
})
export class BookCreateComponent implements OnInit {

  public book: CreateBookResource;

  public error: ErrorMessage = new ErrorMessage(false, '');

  constructor(private _bookService: BookService, private _router: Router) { }

  ngOnInit() {
    this.book = new CreateBookResource('', '')
  }

  onSubmit() {
    this._bookService.createBook(this.book).subscribe(
      data => this._router.navigate(['']),
      (err: HttpErrorResponse) => {
        console.log(`Backend returned code ${err.status}`);
        console.table(err.error);
        const errorResource: ErrorResource = Object.assign(new ErrorResource(null, '', '', ''), err.error);
        this.error = new ErrorMessage(true, errorResource.details);
      }
    );

  }

  onReset() {
    this.book = new CreateBookResource('', '');
  }

  onCancel() {
    this._router.navigate(['']);
  }

  get diagnostic() { return JSON.stringify(this.book); }

}


