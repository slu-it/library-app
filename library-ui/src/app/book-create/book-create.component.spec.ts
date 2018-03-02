import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookCreateComponent } from './book-create.component';
import { FormsModule } from '@angular/forms';
import { BookService } from '../service/book.service';
import { BookServiceMock } from '../service/book.service.mock';
import { BookListComponent } from '../book-list/book-list.component';
import { BookListItemComponent } from '../book-list-item/book-list-item.component';
import { RouterTestingModule } from '@angular/router/testing';

xdescribe('BookCreateComponent', () => {
  let component: BookCreateComponent;
  let fixture: ComponentFixture<BookCreateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule, RouterTestingModule ],
      declarations: [ BookCreateComponent, BookListComponent, BookListItemComponent ],
      providers: [
          { provide: BookService, useClass: BookServiceMock }
        ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
