import { Component, OnInit } from '@angular/core';
import { CreateBookResource } from '../model/create-book-resource';
import { BookService } from '../service/book.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResource } from '../model/error-resource';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'lib-book-create',
  templateUrl: './book-create.component.html',
  styleUrls: ['./book-create.component.css']
})
export class BookCreateComponent implements OnInit {

  public book: CreateBookResource;

  constructor(private _bookService: BookService, private _router: Router, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    this.book = new CreateBookResource('', '')
  }

  onSubmit() {
    this._bookService.createBook(this.book).subscribe(
        data => {
          this._router.navigate(['']);
          this._snackBar.open('Successfully created book', 'dismiss', {
            duration: 5000
          });
        },
      (err: HttpErrorResponse) => {
        console.log(`Backend returned code ${err.status}`);
        console.table(err.error);
        const errorResource: ErrorResource = Object.assign(new ErrorResource(null, '', '', ''), err.error);
        this._snackBar.open('Error creating book ' + errorResource.details, '', {
          duration: 10000
        });
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


