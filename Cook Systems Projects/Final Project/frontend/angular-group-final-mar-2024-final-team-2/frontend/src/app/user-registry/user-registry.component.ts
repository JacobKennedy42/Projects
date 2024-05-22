import { Component, OnInit } from '@angular/core';
import UserInfo from '../models/UserInfo';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddUserComponent } from '../overlays/add-user/add-user.component';
import { Store } from '@ngrx/store';
import { HttpClient } from '@angular/common/http';
import * as fromAuth from 'src/app/auth/auth.reducer';
import User from '../models/User';

const baseUrl = 'http://localhost:8080'

@Component({
  selector: 'app-user-registry',
  templateUrl: './user-registry.component.html',
  styleUrls: ['./user-registry.component.css']
})
export class UserRegistryComponent implements OnInit {

  displayColumns: string[] = ['Name', 'Email', 'Active', 'Admin', 'Status']

  users : UserInfo[] = [];

  constructor (
    private store: Store<fromAuth.AuthState>,
    private http: HttpClient,
    private matDialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.store.select(fromAuth.selectCompanyId).subscribe(companyId => {
      this.http.get<any>(`${baseUrl}/company/${companyId}/users/all`).subscribe(
        (response: User[]) => {
          this.users = response.sort((a, b) => {
            if (a.id && b.id) return b.id - a.id;
            return 0;
          }
        );
        },
        (error) => {
          console.error('Error loading announcements:', error);
        }
      );
    });
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig()
    dialogConfig.width = '600px';
    dialogConfig.height = '750px';
    const dialogRef = this.matDialog.open(AddUserComponent, dialogConfig)
    dialogRef.afterClosed().subscribe((result : User) => {
      if (result) this.users = [result, ...this.users];
    });
  }

}
