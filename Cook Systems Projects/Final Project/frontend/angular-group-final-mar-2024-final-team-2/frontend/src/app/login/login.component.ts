import {Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { AuthState } from '../auth/auth.reducer';
import { loginRequest } from '../auth/auth.actions';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent {

  email: string = ""
  password: string = ""

  constructor(
    private store: Store<AuthState>
  ) {

  }

  signIn() {
      const creds = {
        username: this.email,
        password: this.password
      };
      this.store.dispatch(loginRequest({creds}))
    }


  autofill(user: string, pass: string) {
    this.email = user;
    this.password = pass;
  }
}
