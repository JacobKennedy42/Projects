import { Component, Input, OnInit, ViewChild } from '@angular/core';
import {getListOfTracks} from '../../../services/spotifyAPI'

@Component({
  selector: 'app-song-card',
  templateUrl: './song-card.component.html',
  styleUrls: ['./song-card.component.css']
})
export class SongCardComponent implements OnInit {

  @Input() previewURL : string = '';
  @Input() onClick : any;
  @Input() detailsOn : boolean = false;
  @Input() song : any = undefined;
  @ViewChild('playback') playback: any;
  details: any = undefined;

  constructor() {}

  ngOnInit(): void {
    if (this.song) {
      this.details = {
        track: this.song.name,
        artists: this.song.artists.map((artist: any) => artist.name),
        album: this.song.album.name,
        img: this.song.album.images[0].url,
      }
      console.log(this.song)
    }
  }

  ngAfterViewInit() {
    if (this.playback !== undefined)
      this.playback.nativeElement.volume = .1
  }
}
