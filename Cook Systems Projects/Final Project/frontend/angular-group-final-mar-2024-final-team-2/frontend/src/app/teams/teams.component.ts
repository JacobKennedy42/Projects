import {Component, OnInit} from '@angular/core';
import { Store, select } from '@ngrx/store';
import {map, Observable, Subject, takeUntil} from 'rxjs';
import Team from '../models/Team';
import { Router } from '@angular/router';
import {CompanyService} from "../services/company/company.service";
import Company from "../models/Company";
import * as fromAuth from "../auth/auth.reducer";
import {TeamService} from "../services/team/team.service";

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.css']
})

export class TeamsComponent  implements OnInit {

  isAdmin$ = this.store.select(fromAuth.selectIsAdmin);
  private destroy$ = new Subject<void>();
  companyId! : number;
  teams: Team[] = [];

  constructor(
    private store: Store<fromAuth.AuthState>,
    private companyService: CompanyService,
    private teamService: TeamService,
    private router: Router,
    ) { }

  ngOnInit(): void {
    this.getCompanyId()
    this.getTeamsFromCompany()
  }

  getTeamsFromCompany() {
    this.companyService.getTeamsFromCompany(this.companyId).subscribe({
      next: (teams) => {
        this.teams = teams.sort((a, b) => {
          if (a.id && b.id) return a.id - b.id;
          return 0;
        });
      },
      error: (err) => {
        console.log(err);
      }})
  }

  getCompanyId() {
    this.companyService.getSelectedCompany().subscribe({
      next: (company) => {
        if (company) {
          this.companyId = company.id;
        }},
      error: (err) => {
        console.log(err);
      }})
  }

  showProjects(team: any): void {
    this.router.navigate(['/projects', {team: JSON.stringify(team)}]);
  }

  addTeam($event: any) {
    this.teamService.postTeam($event, this.companyId).subscribe({
      next: (team) => {
        this.getTeamsFromCompany()
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
}
