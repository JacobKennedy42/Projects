import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { StoreModule } from '@ngrx/store';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { TeamsComponent } from './teams/teams.component';
import { ProjectsComponent } from './projects/projects.component';
import { SelectCompanyComponent } from './select-company/select-company.component';
import { UserRegistryComponent } from './user-registry/user-registry.component';
import { HeaderComponent } from './header/header.component';
import { CreateAnnouncementComponent } from './overlays/create-announcement/create-announcement.component';
import { CreateTeamComponent } from './overlays/create-team/create-team.component';
import { CreateProjectComponent } from './overlays/create-project/create-project.component';
import { EditProjectComponent } from './overlays/edit-project/edit-project.component';
import { AddUserComponent } from './overlays/add-user/add-user.component';
import { DropDownComponent } from './utility-views/drop-down/drop-down.component';
import {HttpClientModule} from "@angular/common/http";
import { TextInputComponent } from './utility-views/text-input/text-input.component';
import { AnnouncementCardComponent } from './utility-views/announcement-card/announcement-card.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { authReducer } from './auth/auth.reducer';
import { EffectsModule } from '@ngrx/effects';
import { AuthEffects } from './auth/auth.effects';
import { TeamCardComponent } from './utility-views/team-card/team-card.component';
import {MatDialogModule} from "@angular/material/dialog";
import { LastNameConverterPipe } from './utils/pipes/last-name-converter.pipe';
import { NewTeamItemComponent } from './utility-views/new-team-item/new-team-item.component';
import { DatePipe } from '@angular/common';
import {MatTableModule} from "@angular/material/table";
import {MatMenuModule} from "@angular/material/menu";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { CustomMenuComponent } from './utility-views/custom-menu/custom-menu.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    TeamsComponent,
    ProjectsComponent,
    SelectCompanyComponent,
    UserRegistryComponent,
    HeaderComponent,
    CreateAnnouncementComponent,
    CreateTeamComponent,
    CreateProjectComponent,
    EditProjectComponent,
    AddUserComponent,
    DropDownComponent,
    TextInputComponent,
    AnnouncementCardComponent,
    TeamCardComponent,
    LastNameConverterPipe,
    NewTeamItemComponent,
    CustomMenuComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    StoreModule.forRoot({auth: authReducer}),
    EffectsModule.forRoot([AuthEffects]),
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    MatDialogModule,
    MatTableModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    BrowserAnimationsModule
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
