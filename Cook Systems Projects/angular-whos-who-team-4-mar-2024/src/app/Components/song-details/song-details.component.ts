import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-song-details',
  templateUrl: './song-details.component.html',
  styleUrls: ['./song-details.component.css']
})
export class SongDetailsComponent implements OnInit {

  @Input() details: any = undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
