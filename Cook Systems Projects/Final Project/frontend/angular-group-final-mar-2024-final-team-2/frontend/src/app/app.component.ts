import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'client';
  isScreenSmall: boolean = false;
  isMenuOpen: boolean = false;
  hasChoicePicked: boolean = false;


  constructor() {}

  ngOnInit(): void {}

  receiveIsSmallScreen($event: boolean) {
    this.isScreenSmall = $event;
  }

  receiveIsMenuOpen($event: boolean) {
    this.isMenuOpen = $event;
  }

  receiveChoicePicked($event: boolean) {
    this.isMenuOpen = !$event;

    if ($event) {
      document.querySelector('.nav-background')!.classList.add('slide-up');
    }
  }
}
