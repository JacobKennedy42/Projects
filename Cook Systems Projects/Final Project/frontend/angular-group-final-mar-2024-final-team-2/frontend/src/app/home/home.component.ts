import {Component, OnInit} from '@angular/core';
import * as fromAuth from "../auth/auth.reducer";
import {Store} from "@ngrx/store";
import {selectIsAdmin} from "../auth/auth.reducer";
import Announcement from "../models/Announcement";
import {loremIpsum, mockUserInfo} from "../utils/mocks/mockData";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {CreateAnnouncementComponent} from "../overlays/create-announcement/create-announcement.component";
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';

const baseUrl = 'http://localhost:8080'

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  companyId$ = this.store.select(fromAuth.selectCompanyId);
  isAdmin$ = this.store.select(fromAuth.selectIsAdmin);
  user$ = this.store.select(fromAuth.selectUser);

  announcements!: Announcement[];


  constructor(
    private datePipe: DatePipe,
    private http: HttpClient,
    private store: Store<fromAuth.AuthState>,
    private matDialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.companyId$.subscribe(companyId => {
      this.loadAnnouncementsByCompanyId(companyId);
    });
  }

  loadAnnouncementsByCompanyId(companyId: number): void {
    this.http.get<any[]>(`${baseUrl}/company/${companyId}/announcements`).subscribe(
      (response: Announcement[]) => {
        response = response.sort((a, b) => {
          const dateA = new Date(a.date);
          const dateB = new Date(b.date);
          if (dateB < dateA) return -1;
          if (dateB > dateA) return 1;
          return b.id - a.id;
        });
        this.announcements = response;
      },
      (error) => {
        console.error('Error loading announcements:', error);
      }
    );
  }

  formatDate(date: Date): string {
    return '' + this.datePipe.transform(date, 'longDate');
  }

  openDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = this.user$;
    dialogConfig.width = '490px';
    dialogConfig.height = '440px';
    const dialogRef = this.matDialog.open(CreateAnnouncementComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result : Announcement) => {
      if (result) this.announcements = [result, ...this.announcements];
    })
  }

}
