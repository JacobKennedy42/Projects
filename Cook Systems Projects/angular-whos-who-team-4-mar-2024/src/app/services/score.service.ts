import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScoreService {
  private score = new BehaviorSubject<number> (0)
  private totalQuestions = new BehaviorSubject<number> (5)
  currentScore = this.score.asObservable()
  currentTotalQuestions = this.totalQuestions.asObservable();

  constructor() { }

  incrementScore () {
    console.log(`Incrementing ${this.score}`)
    const c = this.score.getValue();
    this.score.next(c+1);
  }

  resetScore () {
    console.log(`Resetting score`);
    this.score.next(0);
  }


}
