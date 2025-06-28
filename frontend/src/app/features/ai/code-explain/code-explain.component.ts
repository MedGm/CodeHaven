import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AiService } from '../../../core/services/ai.service';
import { CodeExplanationRequest, AiResponse } from '../../../core/models/ai.models';

@Component({
  selector: 'app-code-explain',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './code-explain.component.html',
  styleUrl: './code-explain.component.css'
})
export class CodeExplainComponent implements OnInit {
  explainForm!: FormGroup;
  isLoading = false;
  explanation: AiResponse | null = null;
  errorMessage = '';

  languages = [
    'JavaScript',
    'TypeScript',
    'Python',
    'Java',
    'C++',
    'C#',
    'Go',
    'Rust',
    'PHP',
    'Ruby',
    'Swift',
    'Kotlin',
    'Other'
  ];

  constructor(
    private fb: FormBuilder,
    private aiService: AiService
  ) {}

  ngOnInit() {
    this.initializeForm();
  }

  private initializeForm() {
    this.explainForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(10)]],
      language: ['JavaScript', Validators.required],
      explainLevel: ['beginner', Validators.required]
    });
  }

  onSubmit() {
    if (this.explainForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.explanation = null;

      const request: CodeExplanationRequest = {
        code: this.explainForm.value.code,
        language: this.explainForm.value.language,
        explanationLevel: this.explainForm.value.explainLevel
      };

      this.aiService.explainCode(request).subscribe({
        next: (response: AiResponse) => {
          this.isLoading = false;
          this.explanation = response;
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = 'Failed to explain code. Please try again.';
        }
      });
    }
  }

  getFormattedDate(): string {
    if (!this.explanation?.createdAt) {
      return '';
    }
    
    // Handle different date formats
    try {
      let date: Date;
      
      // Check if createdAt is an array (LocalDateTime from Java)
      if (Array.isArray(this.explanation.createdAt)) {
        const dateArray = this.explanation.createdAt as any[];
        // Array format: [year, month, day, hour, minute, second, nanosecond]
        // Note: JavaScript months are 0-indexed, so subtract 1 from month
        date = new Date(
          dateArray[0], // year
          dateArray[1] - 1, // month (subtract 1 for JS)
          dateArray[2], // day
          dateArray[3], // hour
          dateArray[4], // minute
          dateArray[5] // second
        );
      } else {
        // Handle string date format
        date = new Date(this.explanation.createdAt);
      }
      
      return date.toLocaleString();
    } catch (error) {
      console.error('Date parsing error:', error);
      return 'Unknown date';
    }
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      // Could add a toast notification here
    });
  }
}
