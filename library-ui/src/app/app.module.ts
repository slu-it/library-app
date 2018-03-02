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
import {
  MatButtonModule,
  MatCheckboxModule,
  MatIconModule,
  MatSnackBarModule
} from '@angular/material';
import { LoadBooksResolver } from './resolver/load-books-resolver';
import { LoginComponent } from './login/login.component';
import { LoginService } from './login/login.service';
import { NotAuthorizedComponent } from './not-authorised-component/not-authorized.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NotAuthorizedComponent,
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
    MatIconModule,
    MatSnackBarModule,
    MatButtonModule,
    MatCheckboxModule
  ],
  providers: [LoadBooksResolver, BookService, LoginService],
  bootstrap: [AppComponent]
})
export class AppModule { }
