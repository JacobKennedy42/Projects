import { Component, OnInit } from '@angular/core';
import { getListOfTracks } from 'src/services/spotifyAPI';
import { ScoreService } from '../services/score.service';
import { Router } from '@angular/router';
import { ConfigService } from '../services/config.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {
  
  private songs : any = [];
  songOptions: any = [];
  correct : boolean | undefined;
  questionsSoFar : number = 0;
  totalQuestions : number = 0;
  explicit : boolean = false;

  constructor(private scoreService: ScoreService, private configService: ConfigService, private router: Router) { }

  ngOnInit(): void {
    this.configService.currentExplicit.subscribe(currentExplicit => this.explicit = currentExplicit);

    getListOfTracks(this.explicit).then(
      tracks => {
        this.songs = this.shuffle(tracks)
        console.log(this.songs.length)
        this.songOptions = this.getSongOptions();
      }
    )

    this.scoreService.resetScore();
    this.scoreService.currentTotalQuestions.subscribe(totalQuestions => this.totalQuestions = totalQuestions);
  }

  getSongOptions = () => {
    const songOne = this.songs.splice(0, 1)[0];
    const songTwo = this.songs.splice(0, 1)[0];
    return [songOne, songTwo];
  }

  shuffle = (array:any) => {
    for (let i = array.length - 1; i > 0; i--) { 
      const j = Math.floor(Math.random() * (i + 1)); 
      [array[i], array[j]] = [array[j], array[i]]; 
    } 
    return array; 
  }

  songClickOne = () => {
    this.questionsSoFar++;
    this.correct = this.songOptions[0].popularity >= this.songOptions[1].popularity;
    if (this.correct === true)
      this.scoreService.incrementScore();
  }

  songClickTwo = () => {
    this.questionsSoFar++;
    this.correct = this.songOptions[1].popularity >= this.songOptions[0].popularity;
    if (this.correct === true)
      this.scoreService.incrementScore();
  }

  next = () => {
    this.songOptions = this.getSongOptions();
    this.correct = undefined;

    if (this.questionsSoFar == this.totalQuestions) {
      this.router.navigateByUrl('/gameOver')
    }
  }

}
