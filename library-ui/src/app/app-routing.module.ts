import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {BookListComponent} from "./book-list/book-list.component";
import { BookCreateComponent } from './book-create/book-create.component';
import { LoadBooksResolver } from './resolver/load-books-resolver';
import { LoginComponent } from './login/login.component';
import { NotAuthorizedComponent } from './not-authorised-component/not-authorized.component';

export const BOOK_CREATE_ROUTE = 'create';
export const LOGIN_ROUTE = 'login';
export const NOT_AUTHORISED_ROUTE = 'unauthorized';

const routes: Routes = [
  {
    path: '',
    component: BookListComponent,
    pathMatch: 'full',
    resolve: {
      bookList: LoadBooksResolver
    }
  },
  { path: BOOK_CREATE_ROUTE, component: BookCreateComponent },
  { path: LOGIN_ROUTE, component: LoginComponent },
  {
    path: NOT_AUTHORISED_ROUTE,
    component: NotAuthorizedComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
