import {Component, EventEmitter, Input, Output} from '@angular/core';
import * as fromAuth from "../../auth/auth.reducer";
import {Store} from "@ngrx/store";
import {logout} from "../../auth/auth.actions";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-custom-menu',
  templateUrl: './custom-menu.component.html',
  styleUrls: ['./custom-menu.component.css'],
})
export class CustomMenuComponent {
  isAdmin$ = this.store.select(fromAuth.selectIsAdmin);
  @Input() isScreenSmall: boolean = false;
  @Input() isMenuOpen: boolean = false;

  @Output() hasChoicePicked = new EventEmitter<boolean>();

  constructor(
    private store: Store<fromAuth.AuthState>,) {
  }

  doLogout(): void {
    this.store.dispatch(logout());
    this.hasChoicePicked.emit(true);
  }

  onClick() {
    this.hasChoicePicked.emit(true);
  }

}
