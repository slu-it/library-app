import {Component, Input, OnInit} from '@angular/core';
import {BookResource} from "../model/book-resource";

@Component({
  selector: 'lib-book-list-item',
  templateUrl: './book-list-item.component.html',
  styleUrls: ['./book-list-item.component.css']
})
export class BookListItemComponent implements OnInit {

  @Input()
  public book: BookResource;

  constructor() { }

  ngOnInit() {
  }

}
