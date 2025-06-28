import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AiService } from '../../../core/services/ai.service';
import { BugFixRequest, AiResponse } from '../../../core/models/ai.models';

@Component({
  selector: 'app-bug-fix',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bug-fix.component.html',
  styleUrl: './bug-fix.component.css'
})
export class BugFixComponent implements OnInit {
  bugFixForm: FormGroup;
  isLoading = false;
  fixResult: AiResponse | null = null;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private aiService: AiService
  ) {
    this.bugFixForm = this.fb.group({
      language: ['', Validators.required],
      code: ['', Validators.required],
      errorDescription: ['', Validators.required],
      expectedBehavior: ['']
    });
  }

  ngOnInit() {}

  onSubmit() {
    if (this.bugFixForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.fixResult = null;

      const formData = this.bugFixForm.value;
      const request: BugFixRequest = {
        code: formData.code,
        language: formData.language,
        errorDescription: formData.errorDescription,
        expectedBehavior: formData.expectedBehavior
      };

      this.aiService.bugFix(request).subscribe({
        next: (response) => {
          this.fixResult = response;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Failed to fix bug. Please try again.';
          this.isLoading = false;
        }
      });
    }
  }

  getDisplayedCode(): string {
    if (!this.fixResult) {
      return '';
    }
    
    // First, try to use the clean suggestedCode field
    if (this.fixResult.suggestedCode) {
      return this.fixResult.suggestedCode;
    }
    
    // If suggestedCode is not available, try to parse the response field
    if (this.fixResult.response) {
      try {
        // Check if response is a JSON string
        const parsedResponse = JSON.parse(this.fixResult.response);
        if (parsedResponse.suggestedCode) {
          return parsedResponse.suggestedCode;
        }
      } catch (error) {
        // If parsing fails, try to extract code from markdown-style code blocks
        return this.extractCodeFromResponse(this.fixResult.response) || this.fixResult.response;
      }
    }
    
    return this.fixResult.response || 'No solution generated';
  }

  getSuggestedCode(): string {
    if (!this.fixResult) {
      return '';
    }
    
    // First check if suggestedCode exists directly
    if (this.fixResult.suggestedCode) {
      return this.fixResult.suggestedCode;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.fixResult.response) {
      try {
        const parsedResponse = JSON.parse(this.fixResult.response);
        if (parsedResponse.suggestedCode) {
          return parsedResponse.suggestedCode;
        }
      } catch (error) {
        // If parsing fails, try to extract from the original response
        const extractedCode = this.extractCodeFromResponse(this.fixResult.response);
        if (extractedCode) {
          return extractedCode;
        }
      }
    }
    
    return '';
  }

  getExplanation(): string {
    if (!this.fixResult) {
      return '';
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.fixResult.response) {
      try {
        const parsedResponse = JSON.parse(this.fixResult.response);
        if (parsedResponse.response) {
          return parsedResponse.response;
        }
      } catch (error) {
        // If parsing fails, it's already a plain string
        return this.fixResult.response;
      }
    }
    
    return 'No explanation available';
  }

  getFormattedDate(): string {
    if (!this.fixResult?.createdAt) {
      return '';
    }
    
    // Handle different date formats
    try {
      let date: Date;
      
      // Check if createdAt is an array (LocalDateTime from Java)
      if (Array.isArray(this.fixResult.createdAt)) {
        const dateArray = this.fixResult.createdAt as any[];
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
        date = new Date(this.fixResult.createdAt);
      }
      
      return date.toLocaleString();
    } catch (error) {
      console.error('Date parsing error:', error);
      return 'Unknown date';
    }
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      console.log('Solution copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy solution: ', err);
    });
  }

  applyFix() {
    const suggestedCode = this.getSuggestedCode();
    if (suggestedCode) {
      // Apply the fixed code to the form
      this.bugFixForm.patchValue({ code: suggestedCode });
      console.log('Fix applied to code editor');
      
      // Optional: Show a success message or toast
      // You could add a success message property and show it in the template
    } else {
      console.warn('No suggested code found to apply');
    }
  }

  getSuggestions(): string[] {
    if (!this.fixResult) {
      return [];
    }
    
    // First check if suggestions exist directly
    if (this.fixResult.suggestions && Array.isArray(this.fixResult.suggestions)) {
      return this.fixResult.suggestions;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.fixResult.response) {
      try {
        const parsedResponse = JSON.parse(this.fixResult.response);
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

  getConfidenceScore(): number | null {
    if (!this.fixResult) {
      return null;
    }
    
    // First check if confidence score exists directly
    if (this.fixResult.confidenceScore) {
      return this.fixResult.confidenceScore;
    }
    
    // Check if response is a JSON string that needs parsing
    if (this.fixResult.response) {
      try {
        const parsedResponse = JSON.parse(this.fixResult.response);
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

  private extractCodeFromResponse(response: string): string | null {
    // Look for code blocks in the response (between ``` markers)
    const codeBlockRegex = /```(?:\w+)?\s*\n([\s\S]*?)\n```/g;
    const matches = [...response.matchAll(codeBlockRegex)];
    
    if (matches.length > 0) {
      // Return the last code block (usually the corrected code)
      return matches[matches.length - 1][1].trim();
    }
    
    // If no code blocks found, try to find code patterns
    // Look for common patterns like function definitions, variable declarations, etc.
    const lines = response.split('\n');
    const codeLines: string[] = [];
    let inCodeSection = false;
    
    for (const line of lines) {
      // Look for lines that look like code
      if (line.trim().match(/^(function|const|let|var|class|if|for|while|return|\w+\s*[=:].+|[\w.]+\(.+\))/)) {
        inCodeSection = true;
        codeLines.push(line);
      } else if (inCodeSection && line.trim() === '') {
        // Empty line in code section
        codeLines.push(line);
      } else if (inCodeSection && !line.trim().match(/^[A-Z].*:$/)) {
        // Continue collecting code lines
        codeLines.push(line);
      } else if (inCodeSection) {
        // End of code section
        break;
      }
    }
    
    return codeLines.length > 0 ? codeLines.join('\n').trim() : null;
  }
}
