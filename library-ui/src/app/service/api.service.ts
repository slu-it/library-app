import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BookListResource } from '../model/book-list-resource';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BookResource } from '../model/book-resource';
import { CreateBookResource } from '../model/create-book-resource';
import { BorrowBookResource } from '../model/borrow-book-resource';
import {ApiResource} from "../model/api-resource";

@Injectable()
export class ApiService {

  constructor(private _httpClient: HttpClient) {}

  /**
   * Gets api resource with link info for authorized actions
   * @returns {Observable<ApiResource>}
   */
  public getApiResource(): Observable<ApiResource> {
    return this._httpClient
      .get(environment.libraryService + 'api')
      .map(data => Object.assign(new ApiResource(), data));
  }
}
