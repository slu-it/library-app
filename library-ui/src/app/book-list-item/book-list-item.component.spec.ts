import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookListItemComponent } from './book-list-item.component';
import { FormsModule } from '@angular/forms';
import { BookService } from '../service/book.service';
import { BookListComponent } from '../book-list/book-list.component';
import { BookServiceMock } from '../service/book.service.mock';
import { MOCK_BOOK } from '../model/book-mock';
import { BookResource } from '../model/book-resource';

describe('BookListItemComponent', () => {
  let component: BookListItemComponent;
  let fixture: ComponentFixture<BookListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule ],
      declarations: [ BookListItemComponent, BookListComponent ],
      providers: [ { provide: BookService, useClass: BookServiceMock } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookListItemComponent);
    component = fixture.componentInstance;
    component.book = Object.assign(new BookResource(), MOCK_BOOK);
    component.index = 0;
    fixture.detectChanges();
  });


  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
