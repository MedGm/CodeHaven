import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AiService } from '../../../core/services/ai.service';
import { CodeReviewRequest, AiResponse } from '../../../core/models/ai.models';

@Component({
  selector: 'app-code-review',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './code-review.component.html',
  styleUrl: './code-review.component.css'
})
export class CodeReviewComponent {
  reviewForm: FormGroup;
  isLoading = false;
  reviewResult: AiResponse | null = null;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private aiService: AiService
  ) {
    this.reviewForm = this.fb.group({
      language: ['', Validators.required],
      reviewType: ['GENERAL', Validators.required],
      code: ['', Validators.required],
      context: ['']
    });
  }

  onSubmit() {
    if (this.reviewForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.reviewResult = null;

      const request: CodeReviewRequest = {
        code: this.reviewForm.value.code,
        language: this.reviewForm.value.language,
        reviewType: this.reviewForm.value.reviewType,
        context: this.reviewForm.value.context || undefined
      };

      this.aiService.codeReview(request).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.reviewResult = response;
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Failed to analyze code. Please try again.';
        }
      });
    }
  }

  getFormattedDate(): string {
    if (!this.reviewResult?.createdAt) {
      return '';
    }
    
    // Handle different date formats
    try {
      let date: Date;
      
      // Check if createdAt is an array (LocalDateTime from Java)
      if (Array.isArray(this.reviewResult.createdAt)) {
        const dateArray = this.reviewResult.createdAt as any[];
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
        date = new Date(this.reviewResult.createdAt);
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
      console.log('Review copied to clipboard');
    }).catch(err => {
      console.error('Failed to copy text: ', err);
    });
  }

  getReviewResponse(): string {
    if (!this.reviewResult) {
      return '';
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.reviewResult.response) {
      try {
        const parsedResponse = JSON.parse(this.reviewResult.response);
        if (parsedResponse.response) {
          return parsedResponse.response;
        }
      } catch (error) {
        // If parsing fails, it's already a plain string
        return this.reviewResult.response;
      }
    }
    
    return 'No review available';
  }

  getSuggestions(): string[] {
    if (!this.reviewResult) {
      return [];
    }
    
    // First check if suggestions exist directly
    if (this.reviewResult.suggestions && Array.isArray(this.reviewResult.suggestions)) {
      return this.reviewResult.suggestions;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.reviewResult.response) {
      try {
        const parsedResponse = JSON.parse(this.reviewResult.response);
        if (parsedResponse.suggestions && Array.isArray(parsedResponse.suggestions)) {
          return parsedResponse.suggestions;
        }
      } catch (error) {
        // If parsing fails, return empty array
        return [];
      }
    }
    
    return [];
  }

  getIssues(): string[] {
    if (!this.reviewResult) {
      return [];
    }
    
    // First check if issues exist directly
    if (this.reviewResult.issues && Array.isArray(this.reviewResult.issues)) {
      return this.reviewResult.issues;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.reviewResult.response) {
      try {
        const parsedResponse = JSON.parse(this.reviewResult.response);
        if (parsedResponse.issues && Array.isArray(parsedResponse.issues)) {
          return parsedResponse.issues;
        }
      } catch (error) {
        // If parsing fails, return empty array
        return [];
      }
    }
    
    return [];
  }

  getConfidenceScore(): number | null {
    if (!this.reviewResult) {
      return null;
    }
    
    // First check if confidence score exists directly
    if (this.reviewResult.confidenceScore) {
      return this.reviewResult.confidenceScore;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.reviewResult.response) {
      try {
        const parsedResponse = JSON.parse(this.reviewResult.response);
        if (parsedResponse.confidenceScore) {
          return parsedResponse.confidenceScore;
        }
      } catch (error) {
        // If parsing fails, return null
        return null;
      }
    }
    
    return null;
  }
}
