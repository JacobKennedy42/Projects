import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { AuthService } from './auth.service';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { loginFailure, loginRequest, loginSuccess } from './auth.actions';
import { Router } from '@angular/router';

@Injectable()
export class AuthEffects {
  loginRequest$ = createEffect(() => this.actions$.pipe(
    ofType(loginRequest),
    exhaustMap((action) =>
      this.authService
        .login(action.creds.username, action.creds.password)
        .pipe(
          map((loginSuccessResp) =>
            loginSuccess({loginSuccessResp})
        ),
        catchError((error) => of(loginFailure({loginFailureResp: error})))
      )
    )
  ));

  loginSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(loginSuccess),
        tap(({ loginSuccessResp }) => {
          if (loginSuccessResp.admin) {
            this.router.navigateByUrl('/company');
          } else {
            this.router.navigateByUrl('/');
          }
        })
      ),
      {dispatch: false}
  );

  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private router: Router
  ) {}
}
