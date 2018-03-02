import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookListComponent } from './book-list.component';
import { FormsModule } from '@angular/forms';
import { BookListItemComponent } from '../book-list-item/book-list-item.component';
import { BookService } from '../service/book.service';
import { BookServiceMock } from '../service/book.service.mock';

xdescribe('BookListComponent', () => {
  let component: BookListComponent;
  let fixture: ComponentFixture<BookListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ BookListComponent, BookListItemComponent ],
      providers: [
        { provide: BookService, useClass: BookServiceMock}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
