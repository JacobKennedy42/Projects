import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-labeled-field',
  templateUrl: './labeled-field.component.html',
  styleUrls: ['./labeled-field.component.css']
})
export class LabeledFieldComponent implements OnInit {

  @Input() key: string = '';
  @Input() value: string = '';

  constructor() { }

  ngOnInit(): void {
  }

}
