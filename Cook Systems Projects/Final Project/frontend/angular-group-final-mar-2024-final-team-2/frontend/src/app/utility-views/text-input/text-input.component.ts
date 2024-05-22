import { Component, OnInit, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-text-input',
  // standalone: true,
  // imports: [],
  templateUrl: './text-input.component.html',
  // styleUrl: './text-input.component.css'
})
export class TextInputComponent {

  text: string = ''
  @Input() placeholder: string = '';

  constructor() {}

  ngOnInit(): void {

  }

  setText(text: string) {
    this.text = text;
    this.nameChange.emit(this.text);
  }

  @Output() nameChange: EventEmitter<string> = new EventEmitter<string>();
}
