import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AiService } from '../../../core/services/ai.service';
import { CodeGenerationRequest, AiResponse } from '../../../core/models/ai.models';

@Component({
  selector: 'app-code-generation',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './code-generation.component.html',
  styleUrl: './code-generation.component.css'
})
export class CodeGenerationComponent implements OnInit {
  generationForm: FormGroup;
  isLoading = false;
  generatedCode: AiResponse | null = null;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private aiService: AiService,
    private router: Router
  ) {
    this.generationForm = this.fb.group({
      language: ['', Validators.required],
      codeType: ['', Validators.required],
      description: ['', Validators.required],
      requirements: ['']
    });
  }

  ngOnInit() {}

  onSubmit() {
    if (this.generationForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.generatedCode = null;

      const formData = this.generationForm.value;
      const request: CodeGenerationRequest = {
        description: formData.description,
        language: formData.language,
        codeType: formData.codeType,
        requirements: formData.requirements
      };

      this.aiService.generateCode(request).subscribe({
        next: (response) => {
          this.generatedCode = response;
          this.isLoading = false;
          console.log('Generated code response:', response); // Debug log
        },
        error: (error) => {
          this.errorMessage = error.message || 'Failed to generate code. Please try again.';
          this.isLoading = false;
        }
      });
    }
  }

  getDisplayedCode(): string {
    if (!this.generatedCode) {
      return '';
    }
    
    // First, try to use the clean suggestedCode field
    if (this.generatedCode.suggestedCode) {
      return this.generatedCode.suggestedCode;
    }
    
    // If suggestedCode is not available, try to parse the response field
    if (this.generatedCode.response) {
      try {
        // Check if response is a JSON string
        const parsedResponse = JSON.parse(this.generatedCode.response);
        if (parsedResponse.suggestedCode) {
          return parsedResponse.suggestedCode;
        }
      } catch (error) {
        // If parsing fails, try to extract code from markdown-style code blocks
        return this.extractCodeFromResponse(this.generatedCode.response);
      }
    }
    
    return 'No code generated';
  }

  getFormattedDate(): string {
    if (!this.generatedCode?.createdAt) {
      return '';
    }
    
    // Handle different date formats
    try {
      let date: Date;
      
      // Check if createdAt is an array (LocalDateTime from Java)
      if (Array.isArray(this.generatedCode.createdAt)) {
        const dateArray = this.generatedCode.createdAt as any[];
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
        date = new Date(this.generatedCode.createdAt);
      }
      
      return date.toLocaleString();
    } catch (error) {
      console.error('Date parsing error:', error);
      return 'Unknown date';
    }
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      console.log('Code copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy code: ', err);
    });
  }

  saveAsSnippet() {
    if (this.generatedCode?.response) {
      // Navigate to snippet create page with pre-filled data
      const code = this.getDisplayedCode();
      const language = this.generationForm.get('language')?.value;
      const description = this.generationForm.get('description')?.value;
      
      // You could use query params or state to pass this data
      this.router.navigate(['/snippets/create'], {
        queryParams: {
          code: code,
          language: language,
          description: description
        }
      });
    }
  }

  private extractCodeFromResponse(response: string): string {
    // First try to parse as JSON and extract suggestedCode
    try {
      const parsed = JSON.parse(response);
      if (parsed.suggestedCode) {
        return parsed.suggestedCode;
      }
      if (parsed.response) {
        // If there's a nested response, try to extract code from it
        return this.extractCodeBlocksFromText(parsed.response);
      }
    } catch (error) {
      // Not a JSON string, continue with code block extraction
    }
    
    // Look for code blocks in the response (between ``` markers)
    return this.extractCodeBlocksFromText(response);
  }
  
  private extractCodeBlocksFromText(text: string): string {
    const codeBlockRegex = /```[\w]*\n([\s\S]*?)\n```/g;
    const matches = codeBlockRegex.exec(text);
    return matches ? matches[1].trim() : text;
  }
}
