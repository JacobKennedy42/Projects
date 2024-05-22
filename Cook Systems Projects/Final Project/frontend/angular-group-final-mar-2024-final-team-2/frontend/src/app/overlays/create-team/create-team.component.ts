import {Component, OnInit} from '@angular/core';
import {map, Subject, takeUntil} from "rxjs";
import Company from "../../models/Company";
import User from "../../models/User";
import {CompanyService} from "../../services/company/company.service";
import {MatDialogRef} from "@angular/material/dialog";
import UserInfo from "../../models/UserInfo";

@Component({
  selector: 'app-create-team',
  templateUrl: './create-team.component.html',
  styleUrls: ['./create-team.component.css']
})
export class CreateTeamComponent implements OnInit {
  private destroy$ = new Subject<void>();

  users: User[] = [];
  titleName: string = ""
  description: string = ""
  selectedUsers: User[] = []

  constructor(
    private dialogRef: MatDialogRef<CreateTeamComponent>,
    private companyService : CompanyService,
  ) {
  }

  submit() {
    this.dialogRef.close({
      name: this.titleName,
      description: this.description,
      teammates: this.selectedUsers
    });
  }

  exit() {
    this.dialogRef.close();
  }


  parseEmployees() {
    this.companyService.getSelectedCompany().pipe(
      takeUntil(this.destroy$),
      map((company : Company | null) => company ? company.employees : [])
    ).subscribe((users : User[]) => {
      this.users = users
    })
  }

  ngOnInit(): void {
    this.parseEmployees()
  }

  selected(user: User) {
    if (this.selectedUsers.includes(user)) {
      this.selectedUsers = this.selectedUsers.filter(u => u !== user)
    } else {
      this.selectedUsers.push(user)
    }
  }

  removeUser(user: User) {
    this.selectedUsers = this.selectedUsers.filter(u => u !== user)
  }
}
