import {Component, EventEmitter, Input, Output} from '@angular/core';
import { Router } from '@angular/router';
import { CompanyService } from 'src/app/services/company/company.service';
import Company from "../../models/Company";
import User from "../../models/User";

@Component({
  selector: 'app-drop-down',
  templateUrl: './drop-down.component.html',
  styleUrls: ['./drop-down.component.css']
})
export class DropDownComponent {

  @Input() items: any[] = [];
  @Input() companies: Company[] =  [];
  @Input() employees: User[] = [];
  @Input() forCompanies: boolean = false;
  @Input() forEmployees: boolean = false;
  @Input() forActiveStatus: boolean = false;
  @Input() forAdminStatus: boolean = false;

  @Output() companySelected = new EventEmitter<Company>();
  @Output() userSelected = new EventEmitter<User>();
  @Output() statusSelected = new EventEmitter<boolean>();

  statuses = ['Yes', 'No'];

  constructor () {}

  submitCompany(company: Company) {
    this.companySelected.emit(company);
  }

  submitUser(user: User) {
    this.userSelected.emit(user);
  }

  submitForActiveStatus(status : string) {
    switch (status) {
      case 'Yes':
        this.statusSelected.emit(true);
        break;
      case 'No':
        this.statusSelected.emit(false);
        break;
    }

  }

  submitForAdminStatus(status : string) {
    switch (status) {
      case 'Yes':
        this.statusSelected.emit(true);
        break;
      case 'No':
        this.statusSelected.emit(false);
        break;
    }
  }

  protected readonly JSON = JSON;





}

