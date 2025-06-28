import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeReviewComponent } from './code-review.component';

describe('CodeReviewComponent', () => {
  let component: CodeReviewComponent;
  let fixture: ComponentFixture<CodeReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CodeReviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CodeReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
