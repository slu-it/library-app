
import {Component, Output} from "@angular/core";
import {UserFormData} from "./user.form.data";
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import {LoginService} from "./login.service";
import {Response} from "@angular/http";
import {Router} from "@angular/router";
import {User} from "./user";

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  userdata: UserFormData = new UserFormData('', '');
  message: string = '';

  constructor(private _router:Router, private loginService:LoginService) {

  }

  public login(): void {
    console.info('Logging in');

    this.loginService.login(this.userdata).subscribe(
      (user:User) => {
        console.info(user);
        this._router.navigate(['']);
      },
      (error: Response) => { console.info(error.statusText); this.message = 'Wrong user/password'}
    );
  }

}
