import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import Project from "../../models/Project";
import {ProjectService} from "../../services/project/project.service";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent implements OnInit {
  projectName: string = '';
  description: string = '';
  isActive!: boolean;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: Project,
    private dialogRef: MatDialogRef<CreateProjectComponent>,
    private projectService: ProjectService
  ) { }


  exit() {
    this.dialogRef.close();
  }

  submit(isNew: boolean) {
    this.dialogRef.close({
      project: this.composeProject(),
      isNew: isNew
    })
  }



  composeProject() {
    if (this.data) {
      return {
        id: this.data.id,
        name: this.projectName,
        description: this.description,
        active: this.isActive
      }
    } else {
      return {
        name: this.projectName,
        description: this.description,
        active: this.isActive
      }
    }
  }

  selected($event: boolean) {
    this.isActive = $event;
  }

  ngOnInit(): void {
    this.projectName = this.data.name;
    this.description = this.data.description;
    this.isActive = this.data.active;
  }
}
