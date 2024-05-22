import { Component, Input } from '@angular/core';
import User from 'src/app/models/User';
import UserInfo from "../../models/UserInfo";

@Component({
  selector: 'app-team-card',
  templateUrl: './team-card.component.html',
  styleUrls: ['./team-card.component.css']
})
export class TeamCardComponent {

  @Input() id: number = 0;
  @Input() numProjects = 0;
  @Input() teammates: UserInfo[] = [];
  @Input() description!: string;
  @Input() name!: string;
}
