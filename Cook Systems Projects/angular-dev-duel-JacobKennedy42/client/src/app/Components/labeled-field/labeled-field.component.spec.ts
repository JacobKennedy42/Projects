import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LabeledFieldComponent } from './labeled-field.component';

describe('LabeledFieldComponent', () => {
  let component: LabeledFieldComponent;
  let fixture: ComponentFixture<LabeledFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LabeledFieldComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LabeledFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
