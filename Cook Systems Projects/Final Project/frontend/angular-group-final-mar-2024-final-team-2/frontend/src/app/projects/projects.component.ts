import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Team from '../models/Team';
import { HttpClient } from '@angular/common/http';
import Project from '../models/Project';
import {ProjectService} from "../services/project/project.service";
import {mockProject} from "../utils/mocks/mockData";
import * as fromAuth from "../auth/auth.reducer";
import {Store} from "@ngrx/store";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {CreateProjectComponent} from "../overlays/create-project/create-project.component";


interface DialogCloseResult {
  project: Project;
  isNew: boolean;
}

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  isAdmin$ = this.store.select(fromAuth.selectIsAdmin);
  projects: Project[] = [];
  team!: Team;

  constructor(
    private service: ProjectService,
    private route: ActivatedRoute,
    private router: Router,
    private store: Store<fromAuth.AuthState>,
    private matDialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.team = JSON.parse(params['team']);
      this.loadProjectsByTeamId(this.team.id);
    });
  }

  loadProjectsByTeamId(teamId: number): void {
    this.service.getProjects(teamId).subscribe({
      next: (projects: Project[]) => {
        this.projects = projects.sort((a, b) => {
          if (a.id && b.id) return b.id - a.id;
          return 0;
        });
      },
      error: (error: any) => {
        console.error(error);
      }});
  }

  postProject(project : Project): void {
    this.service.postProject(this.team.id, project).subscribe({
      next: (project: Project) => {
        this.loadProjectsByTeamId(this.team.id)
      },
      error: (error: any) => {
        console.log(error);
      }});
  }

  goBack(): void {
    this.router.navigate(['/teams']); // Replace 'previous-page' with the route of the page you want to navigate back to
  }

  openDialog(project: Project | null) {
    this.matDialog.open(CreateProjectComponent, this.composeDialogConfig(project))
      .afterClosed().subscribe((result : DialogCloseResult) => {
        console.log(result);
        return this.onClose(result);
    })
  }

  composeDialogConfig(project : Project | null = null) {
    const dialogConfig = new MatDialogConfig();
    if (project) {
      dialogConfig.data = project;
    }
    dialogConfig.width = '768px';
    dialogConfig.height = '687px';
    return dialogConfig;
  }

  onClose(result : {project: Project, isNew: boolean}) {
    if (result.isNew) {
      this.postProject(result.project);
    } else {
      this.updateProject(result.project);
    }
  }

  updateProject(project: Project): void {
    this.service.updateProject(this.team.id, project.id!, project).subscribe({
      next: (project: Project) => {
        this.loadProjectsByTeamId(this.team.id);
      },
      error: (error: any) => {
        console.log(error);
      }});
  }

  protected readonly mockProject = mockProject;

}
