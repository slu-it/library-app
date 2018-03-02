import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {LoginService} from "../login/login.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private _auth: LoginService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('getUserAuth: ' + this._auth.getUserAuth());

    const authReq = req.clone({
      headers: req.headers
        .append("Authorization", "Basic " + this._auth.getUserAuth())
        .append("X-Requested-With", "XMLHttpRequest")
        .append('Content-Type', 'application/json')
    });
    return next.handle(authReq);
  }

}
