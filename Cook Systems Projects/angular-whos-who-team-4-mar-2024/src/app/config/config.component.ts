import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { ConfigService } from '../services/config.service';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {
  @Input() config!: any;
  @Output() configChange = new EventEmitter<any>();
  explicit : boolean = false;

  constructor(private configService: ConfigService) { }

  ngOnInit(): void {
      this.configService.currentExplicit.subscribe(currentExplicit => this.explicit = currentExplicit);
  }

  selectedMarketsChanged(markets: string[]) {
    this.config.markets = markets;
    this.configChange.emit(this.config);
  }


}
