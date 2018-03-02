import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs/Observable";
import {LoginService} from "../login/login.service";

@Injectable()
export class IsAuthenticatedGuard implements CanActivate {

  private _store: Storage;

  constructor(private _router: Router, private _auth: LoginService) {
    this._store = sessionStorage;
  }

  public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    console.log('store: lib-auth: ' + this._store.getItem("lib-auth"));
    console.log('auth.getUserAuth(): ' + this._auth.getUserAuth());
    if (this._store.getItem("lib-auth") != null && this._auth.getUserAuth() != null) {
      console.log('authenticated');
      return true;
    } else {
      console.log('not authenticated');
      this._router.navigateByUrl('login');
      return false;
    }
  }

}
