import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewTeamItemComponent } from './new-team-item.component';

describe('NewTeamItemComponent', () => {
  let component: NewTeamItemComponent;
  let fixture: ComponentFixture<NewTeamItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewTeamItemComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewTeamItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
