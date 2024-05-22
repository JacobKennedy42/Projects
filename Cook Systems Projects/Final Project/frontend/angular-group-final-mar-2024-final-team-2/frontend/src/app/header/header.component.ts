import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {CompanyService} from "../services/company/company.service";
import { Store } from '@ngrx/store';
import * as fromAuth from '../auth/auth.reducer'
import { logout } from '../auth/auth.actions';
import {Subscription} from "rxjs";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Output() isMenuOpenOutput = new EventEmitter<boolean>();
  @Output() isSmallScreenOutput = new EventEmitter<boolean>();

  @Input() isMenuOpenInput: boolean = false;
  @Input() hasChoicePicked: boolean = false;
  @Input() isScreenSmallInput: boolean = false;

  screenWidth: number = window.innerWidth;
  breakpointSubscription!: Subscription;

  loggedIn$ = this.store.select(fromAuth.selectLoggedIn);
  firstLast$ = this.store.select(fromAuth.selectFirstLast);
  isAdmin$ = this.store.select(fromAuth.selectIsAdmin);

  constructor(
    private breakpointObserver: BreakpointObserver,
    private store: Store<fromAuth.AuthState>,
  ) { }

  doLogout(): void {
    this.store.dispatch(logout());
  }

  composeBreakpointObserver(): void {
    this.breakpointSubscription = this.breakpointObserver.observe([
      '(max-width: 770px)'
    ]).subscribe(result => {
      this.isSmallScreenOutput.emit(result.matches);
      this.screenWidth = window.innerWidth;
    });
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.composeBreakpointObserver()
  }

  toggleMenu() {
    this.isMenuOpenOutput.emit(!this.isMenuOpenInput);
  }
}
