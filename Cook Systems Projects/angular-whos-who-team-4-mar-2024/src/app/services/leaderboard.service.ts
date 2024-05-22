import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface LeaderboardItem {
  name: string;
  score: number;
}

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private leaderBoard = new BehaviorSubject<LeaderboardItem[]> ([])
  currentleaderBoard = this.leaderBoard.asObservable()

  constructor() { }

  addToLeaderBoard (name: string, score: number) {
    let newLeaderboard = this.leaderBoard.getValue()
    let insertionIndex = 0;
    while (insertionIndex < newLeaderboard.length && newLeaderboard[insertionIndex].score > score)
      insertionIndex++;
    newLeaderboard.splice(insertionIndex, 0, {name: name, score: score})
    this.leaderBoard.next(newLeaderboard)
  }
}
