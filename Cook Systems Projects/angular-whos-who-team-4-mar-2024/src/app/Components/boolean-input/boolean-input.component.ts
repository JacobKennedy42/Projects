import { Component, Input, OnInit } from '@angular/core';
import { ConfigService } from 'src/app/services/config.service';

@Component({
  selector: 'app-boolean-input',
  templateUrl: './boolean-input.component.html',
  styleUrls: ['./boolean-input.component.css']
})
export class BooleanInputComponent implements OnInit {

  @Input() startingValue : boolean = false;

  constructor(private configService: ConfigService) {}

  ngOnInit(): void {
  }

  setValue = (value:boolean) => {
    this.configService.setExplicit(value);
  }

}
