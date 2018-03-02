import {ResourceLink} from "./resource-link";


export class ApiResource {
  public _links: ApiResourceLinks;
}

class ApiResourceLinks {
  public self: ResourceLink;
  public getBooks?: ResourceLink;
  public addBook?: ResourceLink;
}
