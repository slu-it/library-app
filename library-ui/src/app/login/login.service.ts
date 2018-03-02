
import {Injectable} from "@angular/core";
import {UserFormData} from "./user.form.data";
import { HttpClient, HttpHeaders, HttpErrorResponse } from "@angular/common/http";
import {Observable} from "rxjs";
import { environment } from '../../environments/environment';
import {User} from "./user";
import {BookResource} from "../model/book-resource";

@Injectable()
export class LoginService {

  constructor(private http: HttpClient) {
  }

  login(userdata: UserFormData): Observable<User> {

    return this.http.get(
      environment.libraryService + 'userinfo',
      { headers: new HttpHeaders()
          .append("Authorization", "Basic " + btoa(userdata.username + ':' + userdata.password))
          .append("X-Requested-With", "XMLHttpRequest")
          .append('Content-Type', 'application/json')
      }).map(data => Object.assign(new User(), data));
  }
}
