import {Component, EventEmitter, Output} from '@angular/core';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {CreateTeamComponent} from "../../overlays/create-team/create-team.component";
import Team from "../../models/Team";
import UserInfo from "../../models/UserInfo";


@Component({
  selector: 'app-new-team-item',
  templateUrl: './new-team-item.component.html',
  styleUrls: ['./new-team-item.component.css']
})

export class NewTeamItemComponent {
  @Output() newTeam = new EventEmitter<any>();

  constructor(
    private matDialog: MatDialog,
  ) { }

  createNewTeam() {
    this.openDialog()
  }

  openDialog() {
    this.matDialog.open(CreateTeamComponent, this.composeDialogConfig())
      .afterClosed().subscribe((result: Team) => {
      if (result) {
        this.newTeam.emit(this.composeTeam(result));
      }
    });
  }

  composeDialogConfig() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '768px';
    dialogConfig.height = '687px';
    return dialogConfig;
  }

  composeTeam(team: Team) {
    return {
      name: team.name,
      description: team.description,
      teammateIds: team.teammates.map((teammate: UserInfo) => teammate.id)
    }
  }


}
