import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-announcement-card',
  templateUrl: './announcement-card.component.html',
  styleUrls: ['./announcement-card.component.css']
})
export class AnnouncementCardComponent {

  @Input() username: string = '';
  @Input() postedAt: string = '';
  @Input() message: string = '';
  @Input() title: string = '';

}
