import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BugFixComponent } from './bug-fix.component';

describe('BugFixComponent', () => {
  let component: BugFixComponent;
  let fixture: ComponentFixture<BugFixComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BugFixComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BugFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
