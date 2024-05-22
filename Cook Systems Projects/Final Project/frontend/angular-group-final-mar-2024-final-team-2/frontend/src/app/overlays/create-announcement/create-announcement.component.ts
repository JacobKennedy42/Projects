import {Component, EventEmitter, Inject, Input, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import * as fromAuth from 'src/app/auth/auth.reducer';
import { Store } from '@ngrx/store';
import { HttpClient } from '@angular/common/http';
import Announcement from 'src/app/models/Announcement';

const baseUrl = 'http://localhost:8080'

@Component({
  selector: 'app-create-announcement',
  templateUrl: './create-announcement.component.html',
  styleUrls: ['./create-announcement.component.css']
})
export class CreateAnnouncementComponent {
  @Input() parentState: Announcement[] = [];
  @Output() parentStateChange = new EventEmitter<Announcement[]>();


  firstLast$ = this.store.select(fromAuth.selectFirstLast);
  teamId$ = this.store.select(fromAuth.selectAdminTeamId);
  title : string = "";
  message : string = "";

  constructor(
    private store: Store<fromAuth.AuthState>,
    private http: HttpClient,
    @Inject(MAT_DIALOG_DATA) public data: Announcement,
    private dialogRef: MatDialogRef<CreateAnnouncementComponent>
  ) {
  }

  submit() {
    this.store.select(fromAuth.selectCompanyId).subscribe(companyId => {
      this.store.select(fromAuth.selectUserId).subscribe(userId => {
        this.store.select(fromAuth.selectAdminTeamId).subscribe(teamId => {
          const request = {
            title: this.title,
            message: this.message,
            authorId: userId,
            teamId
          }
          this.http.post<any>(`${baseUrl}/company/${companyId}/user/${userId}/announcements`, request).subscribe(
            (response: Announcement) => {
              this.dialogRef.close(response);
            },
            (error) => {
              console.error('Error loading announcements:', error);
            }
          );
        });
        
      });
    });
  }

  exit() {
    this.dialogRef.close();
  }
}
