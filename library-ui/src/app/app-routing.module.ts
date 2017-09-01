import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {BookListComponent} from "./book-list/book-list.component";
import { BookCreateComponent } from './book-create/book-create.component';
import { LoadBooksResolver } from './resolver/load-books-resolver';

const routes: Routes = [
  {
    path: '',
    component: BookListComponent,
    pathMatch: 'full',
    resolve: {
      bookList: LoadBooksResolver
    }
  },
  { path: 'create', component: BookCreateComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
