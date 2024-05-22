import { Component, OnInit } from '@angular/core';

// the root component holds global state
type PageState = "Home" | "Game" | "Leaderboard" | "Config";

const defaultConfig = () => ({
  market: 'US',
});

@Component({
  selector: 'app-root',
  templateUrl: './root.component.html',
  styleUrls: ['./root.component.css']
})
export class RootComponent implements OnInit {

  private pageState: PageState = "Home";
  private config: any;

  constructor() { }

  ngOnInit(): void {
    this.config = localStorage.getItem('config') ?? defaultConfig();
    localStorage.setItem('config', this.config); // if it was null before, it's not anymore
  }

  handleConfigChange(config: Object) {
    this.config = config;
    localStorage.setItem('config', this.config);
  }

  startGame() {
    this.pageState = 'Game';
  }

  goToLeaderBoard() {
    this.pageState = 'Leaderboard';
  }



}
