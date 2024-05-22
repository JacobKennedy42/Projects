import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import Team from "../../models/Team";
import {baseUrl} from "../project/project.service";

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(
    private http: HttpClient
  ) { }

  postTeam(team: Team, companyId: number) {
    return this.http.post(`${baseUrl}/company/${companyId}/team`, team)
  }
}
