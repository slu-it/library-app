
import {Injectable} from "@angular/core";
import {UserFormData} from "./user.form.data";
import { HttpClient, HttpHeaders, HttpErrorResponse } from "@angular/common/http";
import {Observable} from "rxjs";
import { environment } from '../../environments/environment';
import {User} from "./user";
import {BookResource} from "../model/book-resource";

@Injectable()
export class LoginService {

  private _userAuth: string;

  constructor(private http: HttpClient) {
  }

  login(userdata: UserFormData): Observable<User> {

    this._userAuth = btoa(userdata.username + ':' + userdata.password);
    console.log('login#userAuth: ' + this._userAuth);
    return this.http.get(
      environment.libraryService + 'userinfo',
      { headers: new HttpHeaders()
      }).map(data => Object.assign(new User(), data));
  }

  logout() {
    console.log('logout()');
    this._userAuth = null;
  }

  public getUserAuth(): string {
    return this._userAuth;
  }
}
