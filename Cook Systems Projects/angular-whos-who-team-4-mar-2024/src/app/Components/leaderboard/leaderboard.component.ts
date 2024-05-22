import { Component, OnInit } from '@angular/core';
import { LeaderboardService, LeaderboardItem } from 'src/app/services/leaderboard.service';

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderboard: LeaderboardItem[] = [];

  constructor(private leaderBoardService: LeaderboardService) { }

  ngOnInit(): void {
    this.leaderBoardService.currentleaderBoard.subscribe(currentLeaderBoard => this.leaderboard = currentLeaderBoard)
  }

  onSubmit() {
    console.log("click")
    this.leaderBoardService.addToLeaderBoard("newName", 10);
    console.log(this.leaderBoardService.currentleaderBoard);
  }

}
