import {BookResource} from "./book-resource";
import {ResourceLink} from "./resource-link";

export class BookListResource {
  public _embedded?: BookResourceEmbedded;
  public _links: BookListResourceLinks;
}

class BookResourceEmbedded {
  public books?: BookResource[];
}

class BookListResourceLinks {
  self: ResourceLink;
}
