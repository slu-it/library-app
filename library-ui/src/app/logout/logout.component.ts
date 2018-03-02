
import {Component, OnInit} from "@angular/core";
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import {Router} from "@angular/router";
import {LoginService} from "../login/login.service";

@Component({
  selector: 'logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  private _store: Storage;

  constructor(private _router:Router, private loginService:LoginService) {
    this._store = sessionStorage;
  }

  ngOnInit(): void {
    this.logout();
  }

  public logout(): void {
    console.info('Logging out');
    this.loginService.logout();
    this._store.removeItem('lib-auth');
    this._router.navigateByUrl('');
  }

}
