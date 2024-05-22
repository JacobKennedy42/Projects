import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule } from "@angular/forms";
import { RouterModule, Routes } from "@angular/router";

import { AppComponent } from "./app.component";
import { HomeComponent } from "./home/home.component";
import { SongCardComponent } from './Components/song-card/song-card.component';
import { RootComponent } from './root/root.component';
import { GameComponent } from './game/game.component';
import { ConfigComponent } from './config/config.component';
import { LinkButtonComponent } from './Components/link-button/link-button.component';
import { GameOverComponent } from './game-over/game-over.component';
import { LeaderboardComponent } from './Components/leaderboard/leaderboard.component';
import { TextInputComponent } from './Components/text-input/text-input.component';
import { BooleanInputComponent } from './Components/boolean-input/boolean-input.component';
import { SongDetailsComponent } from './Components/song-details/song-details.component';

const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "game", component: GameComponent},
  { path: "config", component: ConfigComponent},
  { path: "gameOver", component: GameOverComponent}
];

@NgModule({
  declarations: [AppComponent, HomeComponent, SongCardComponent, RootComponent, GameComponent, ConfigComponent, LinkButtonComponent, GameOverComponent, LeaderboardComponent, TextInputComponent, BooleanInputComponent, SongDetailsComponent],
  imports: [BrowserModule, FormsModule, RouterModule.forRoot(routes)],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
