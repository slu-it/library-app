import { TestBed, inject } from '@angular/core/testing';

import { BookService } from './book.service';
import {BookListResource} from "../model/book-list-resource";
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Observable } from 'rxjs/Observable';

describe('BookService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookService]
    });
  });

  it('should be created', inject([BookService], (service: BookService) => {
    expect(service).toBeTruthy();
  }));

  /*
  it('findAllBooks() should return expected contents', inject([BookService], (service: BookService) => {
    expect(service.findAllBooks()).toBe(Observable.of(new BookListResource()));
  }));*/
});
