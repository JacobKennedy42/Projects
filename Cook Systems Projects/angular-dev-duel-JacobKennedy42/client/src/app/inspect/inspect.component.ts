import { Component, Input, OnInit } from '@angular/core';
import { any } from 'joi';
import { UserService } from 'src/user.service';

@Component({
  selector: 'app-inspect',
  templateUrl: './inspect.component.html',
  styleUrls: ['./inspect.component.css']
})
export class InspectComponent implements OnInit {

  username: string = ""

  profile : object = {};


  constructor(private userService: UserService) { }

  ngOnInit(): void {
  }

  receiveUsername(valueEmitted: string) {
    this.username = valueEmitted;
  }

  profileSet() {return Object.keys(this.profile).length > 0}

  onSubmit() {
    this.userService.inspectUser(this.username)
    .then((data:any) => {
        this.profile = data;
    })
    .catch(response => {
      if (response.status == 404)
        alert("User not found");
      else
        alert(response.error.message)
    });
  }



}
