import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {RouterGuardService} from "./services/router-guard.service";
import {LoginComponent} from "./login/login.component";
import {ProjectsComponent} from "./projects/projects.component";
import {SelectCompanyComponent} from "./select-company/select-company.component";
import {TeamsComponent} from "./teams/teams.component";
import {UserRegistryComponent} from "./user-registry/user-registry.component";

const routes: Routes = [

  { path: "", component: HomeComponent, canActivate: [RouterGuardService]},
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [RouterGuardService] },
  { path: 'projects', component: ProjectsComponent, canActivate: [RouterGuardService] },
  { path: 'company', component: SelectCompanyComponent, canActivate: [RouterGuardService] },
  { path: 'teams', component: TeamsComponent, canActivate: [RouterGuardService] },
  { path: 'user-registry', component: UserRegistryComponent, canActivate: [RouterGuardService] },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
