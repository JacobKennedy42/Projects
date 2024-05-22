import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import User from 'src/app/models/User';
import * as fromAuth from 'src/app/auth/auth.reducer';

const baseUrl = 'http://localhost:8080'

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

  firstName: string = ""
  lastName: string = ""
  email: string = ""
  phone: string = ""
  password: string = ""
  passwordRepeat: string = ""
  adminStatus: boolean = false;

  constructor (
    private store: Store<fromAuth.AuthState>,
    private http: HttpClient,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<AddUserComponent>
  ) {}

  ngOnInit(): void {
  }

  submit() {
    const request = {
      credentials: {
        username: this.firstName + this.lastName,
        password: this.password
      },
      profile: {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
        phone: this.phone
      },
      admin: this.adminStatus
    }

    this.store.select(fromAuth.selectCompanyId).subscribe(companyId => {
      console.log(request)
      this.http.post<any>(`${baseUrl}/users/company/${companyId}`, request).subscribe(
        (response: User) => {
          console.log(response)
          this.dialogRef.close(response);
        },
        (error) => {
          console.error('Error loading announcements:', error);
        }
      );
    });
  }

  exit() {
    this.dialogRef.close();
  }

  receiveAdminStatus($event: boolean) {
    this.adminStatus = $event;
  }
}
