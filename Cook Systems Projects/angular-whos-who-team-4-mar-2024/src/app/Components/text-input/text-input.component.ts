import { Component, OnInit, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.css']
})
export class TextInputComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  name: string = '';

  setName(name: string) {
    this.name = name;
    this.nameChange.emit(this.name);
  }

  @Output() nameChange: EventEmitter<string> = new EventEmitter<string>();
}
