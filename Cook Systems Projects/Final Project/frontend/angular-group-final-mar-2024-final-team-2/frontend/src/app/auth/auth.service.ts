import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import User from "../models/User";

const baseUrl = 'http://localhost:8080'

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    const request = {
      username,
      password
    }
    return this.http.post<User>(`${baseUrl}/users/login`, request);
  }
}
