import {ResourceLink} from "./resource-link";


export class BookResource {
  public isbn: string;
  public title: string;
  public authors: string[];
  public numberOfPages: number;
  public borrowed?: BorrowedResource;
  public _links: BookResourceLinks;
}

class BorrowedResource {
  public by: string;
  public on: Date;
}

class BookResourceLinks {
  public self: ResourceLink;
  public delete?: ResourceLink;
  public borrow?: ResourceLink;
  public return?: ResourceLink;
}
