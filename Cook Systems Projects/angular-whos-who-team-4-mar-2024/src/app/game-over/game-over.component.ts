import { Component, OnInit } from '@angular/core';
import { ScoreService } from '../services/score.service';
import { LeaderboardService } from '../services/leaderboard.service';

@Component({
  selector: 'app-game-over',
  templateUrl: './game-over.component.html',
  styleUrls: ['./game-over.component.css']
})
export class GameOverComponent implements OnInit {

  score : number = 0;
  totalQuestions : number = 0;
  name : string = '';
  submitted: boolean = false;

  constructor(private scoreService: ScoreService, private leaderBoardService: LeaderboardService) { }

  ngOnInit(): void {
    this.scoreService.currentScore.subscribe(score => this.score = score);
    this.scoreService.currentTotalQuestions.subscribe(totalQuestions => this.totalQuestions = totalQuestions);
  }

  onSubmit = () => {
    this.leaderBoardService.addToLeaderBoard(this.name, this.score);
    this.submitted = true;
  }

  receiveName(valueEmitted: string) {
    this.name = valueEmitted;
  }

}
