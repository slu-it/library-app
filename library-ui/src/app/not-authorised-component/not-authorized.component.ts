import {Component} from '@angular/core';
import {Router} from '@angular/router';

/*
 * Not Authorized.
 */
@Component({
    selector: 'ss-not-authorized',
    templateUrl: './not-authorized.component.html'
})
export class NotAuthorizedComponent {

    constructor(private _router: Router) {
    }

    public routeToStartPage(): void {
        this._router.navigate(['']);
    }
}
