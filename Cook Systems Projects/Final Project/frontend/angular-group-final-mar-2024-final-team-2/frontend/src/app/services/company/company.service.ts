import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import Company from "../../models/Company";
import {mockCompany, mockCompany2, mockCredentials} from "../../utils/mocks/mockData";
import Team from "../../models/Team";
import User from "../../models/User";
import {Projects} from "@angular/cli/lib/config/workspace-schema";
import {BehaviorSubject, map, takeUntil} from "rxjs";
import { Store, select } from '@ngrx/store';
import * as fromAuth from 'src/app/auth/auth.reducer';



@Injectable({
  providedIn: 'root'
})
export class CompanyService {

  private selectedCompany = new BehaviorSubject<Company | null>(null);

  constructor(
    private store: Store<fromAuth.AuthState>,
    private http : HttpClient
  ) {
    this.store.pipe(select(fromAuth.selectCompany)).subscribe(company => {
      this.selectedCompany.next(company);
    });
  }

  getSelectedCompany() {
    return this.selectedCompany.asObservable();
  }

  getCompanies() {
    return this.http.get<[Company]>("http://localhost:8080/company")
  }

  getTeamsFromCompany(id : number) {
    return this.http.get<[Team]>(`http://localhost:8080/company/${id}/teams`)
  }

  postTeamToCompany(id : number, team : Team, isAdmin : boolean) {
    const request = {
      credentials: mockCredentials,
      team: team,
      isAdmin: isAdmin
    }
    return this.http.post<Team>(`http://localhost:8080/companies/${id}/teams`, request)
  }

  getUsersFromCompany(id : number) {
    return this.http.get<[User]>(`http://localhost:8080/companies/${id}/users`)
  }


  getCompanyProjects(id : number) {
    return this.http.get<[Projects]>(`http://localhost:8080/companies/${id}/projects`)
  }

  postTeamToCompanyMock(id : number, team : Team, isAdmin : boolean) {
    return mockCompany.teams.push(team);
  }

  getCompanyMock() {
    return mockCompany;
  }

  getTeamsFromCompanyMock() {
    return mockCompany.teams;
  }

  getUsersFromCompanyMock() {
    return mockCompany.employees;
  }

  getUsersFromCompany2Mock() {
    return mockCompany2.employees;
  }
}
