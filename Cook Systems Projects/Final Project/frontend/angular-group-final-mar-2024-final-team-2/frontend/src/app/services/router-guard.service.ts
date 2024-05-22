import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Router} from "@angular/router";
import { Store, select } from '@ngrx/store';
import { AuthState, selectAuthState } from '../auth/auth.reducer';
import { Observable, map, take } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RouterGuardService {

  constructor(private store: Store<AuthState>, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.store.pipe(
      select(selectAuthState),
      take(1),
      map(authState => {
        if (!authState.loggedIn) {
          this.router.navigate(['/login']);
          return false;
        }
        return true;
      })
    );
  }
}
