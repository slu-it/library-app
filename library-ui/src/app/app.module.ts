import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BookListComponent } from './book-list/book-list.component';
import { BookListItemComponent } from './book-list-item/book-list-item.component';
import { BookService } from './service/book.service';
import { HttpClientModule } from '@angular/common/http';
import { BookCreateComponent } from './book-create/book-create.component';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MdButtonModule, MdCheckboxModule, MdIconModule, MdSnackBarModule } from '@angular/material';
import { LoadBooksResolver } from './resolver/load-books-resolver';

@NgModule({
  declarations: [
    AppComponent,
    BookListComponent,
    BookListItemComponent,
    BookCreateComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    MdIconModule,
    MdSnackBarModule,
    MdButtonModule,
    MdCheckboxModule
  ],
  providers: [LoadBooksResolver, BookService],
  bootstrap: [AppComponent]
})
export class AppModule { }
