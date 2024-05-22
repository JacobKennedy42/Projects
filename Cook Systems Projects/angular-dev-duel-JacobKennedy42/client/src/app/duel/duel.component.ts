import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/user.service';

@Component({
  selector: 'app-duel',
  templateUrl: './duel.component.html',
  styleUrls: ['./duel.component.css']
})
export class DuelComponent implements OnInit {
  usernameOne: string = ""
  usernameTwo: string = ""
  profileOneHash : number = 0;
  profileTwoHash : number = 0;
  profileOne : any = {};
  profileTwo : any = {};

  constructor(private userService: UserService) { }

  ngOnInit(): void {
  }

  receiveUsernameOne(valueEmitted: string) {
    this.usernameOne = valueEmitted;
  }

  receiveUsernameTwo(valueEmitted: string) {
    this.usernameTwo = valueEmitted;
  }

  profilesSet() {return (Object.keys(this.profileOne).length > 0) || (Object.keys(this.profileTwo).length > 0)}

  hashOf(s : string) {return s.split('').reduce((hash, char) => {return char.charCodeAt(0) + (hash << 6) + (hash << 16) - hash;}, 0);}

  profileOneWins() {return this.hashOf(this.profileOne.username) > this.hashOf(this.profileTwo.username)}

  onSubmit() {
    this.userService.duelUsers(this.usernameOne, this.usernameTwo)
    .then((data: any) => {
      [this.profileOne, this.profileTwo] = data;
      this.profileOneHash = this.hashOf(this.profileOne.username);
      this.profileTwoHash = this.hashOf(this.profileTwo.username);
    })
    .catch(response => {
      console.log(response)
      if (response.status == 404)
        alert("User(s) not found");
      else if (response.status == 400)
        alert("Please enter usernames in both fields");
      else
        alert(response.error.message)
    });
  }
}
