import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AiService } from '../../../core/services/ai.service';
import { CodeOptimizationRequest, AiResponse } from '../../../core/models/ai.models';

@Component({
  selector: 'app-code-optimize',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './code-optimize.component.html',
  styleUrl: './code-optimize.component.css'
})
export class CodeOptimizeComponent implements OnInit {
  optimizeForm!: FormGroup;
  isLoading = false;
  optimizedCode: AiResponse | null = null;
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

  optimizationTypes = [
    { value: 'PERFORMANCE', label: 'Performance - Speed improvements' },
    { value: 'MEMORY', label: 'Memory - Reduce memory usage' },
    { value: 'READABILITY', label: 'Readability - Code clarity' },
    { value: 'GENERAL', label: 'General - Overall optimization' }
  ];

  constructor(
    private fb: FormBuilder,
    private aiService: AiService
  ) {}

  ngOnInit() {
    this.initializeForm();
  }

  private initializeForm() {
    this.optimizeForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(10)]],
      language: ['JavaScript', Validators.required],
      optimizationType: ['GENERAL', Validators.required],
      context: ['']
    });
  }

  onSubmit() {
    if (this.optimizeForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.optimizedCode = null;

      const request: CodeOptimizationRequest = {
        code: this.optimizeForm.value.code,
        language: this.optimizeForm.value.language,
        optimizationType: this.optimizeForm.value.optimizationType,
        context: this.optimizeForm.value.context
      };

      this.aiService.optimizeCode(request).subscribe({
        next: (response: AiResponse) => {
          this.isLoading = false;
          this.optimizedCode = response;
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = 'Failed to optimize code. Please try again.';
        }
      });
    }
  }

  getFormattedDate(): string {
    if (!this.optimizedCode?.createdAt) {
      return '';
    }
    
    // Handle different date formats
    try {
      let date: Date;
      
      // Check if createdAt is an array (LocalDateTime from Java)
      if (Array.isArray(this.optimizedCode.createdAt)) {
        const dateArray = this.optimizedCode.createdAt as any[];
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
        date = new Date(this.optimizedCode.createdAt);
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

  getSuggestedCode(): string {
    if (!this.optimizedCode) return '';
    
    // Try to extract the suggestedCode field first
    if ((this.optimizedCode as any).suggestedCode) {
      return (this.optimizedCode as any).suggestedCode;
    }
    
    // If not available, try to extract code from response using regex
    const codeBlockRegex = /```[\w]*\n([\s\S]*?)\n```/g;
    const matches = codeBlockRegex.exec(this.optimizedCode.response);
    
    if (matches && matches[1]) {
      return matches[1].trim();
    }
    
    // If no code block found, return empty string
    return '';
  }
}
