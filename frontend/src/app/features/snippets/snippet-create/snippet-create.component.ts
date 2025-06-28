import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SnippetService } from '../../../core/services/snippet.service';
import { SnippetCreate } from '../../../core/models/content.models';

@Component({
  selector: 'app-snippet-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './snippet-create.component.html',
  styleUrl: './snippet-create.component.css'
})
export class SnippetCreateComponent implements OnInit {
  snippetForm!: FormGroup;
  isLoading = false;
  errorMessage = '';
  availableLanguages: string[] = [];

  defaultLanguages = [
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
    'HTML',
    'CSS',
    'SQL',
    'Shell',
    'Other'
  ];

  constructor(
    private fb: FormBuilder,
    private snippetService: SnippetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadLanguages();
  }

  initializeForm(): void {
    this.snippetForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      language: ['', Validators.required],
      code: ['', Validators.required],
      tags: [''],
      isPublic: [true]
    });
  }

  loadLanguages(): void {
    this.snippetService.getAllLanguages().subscribe({
      next: (languages) => {
        this.availableLanguages = languages.length > 0 ? languages : this.defaultLanguages;
      },
      error: (error) => {
        console.error('Error loading languages:', error);
        this.availableLanguages = this.defaultLanguages;
      }
    });
  }

  onSubmit(): void {
    if (this.snippetForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const formValue = this.snippetForm.value;
      const snippetData: SnippetCreate = {
        title: formValue.title.trim(),
        description: formValue.description?.trim() || '',
        language: formValue.language,
        code: formValue.code,
        tags: formValue.tags ? 
          formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag.length > 0) : 
          [],
        isPublic: formValue.isPublic
      };

      this.snippetService.createSnippet(snippetData).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/snippets', response.id]);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Failed to create snippet. Please try again.';
          console.error('Error creating snippet:', error);
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.snippetForm.controls).forEach(key => {
      const control = this.snippetForm.get(key);
      control?.markAsTouched();
    });
  }

  onCancel(): void {
    this.router.navigate(['/snippets']);
  }

  // Helper methods for template
  isFieldInvalid(fieldName: string): boolean {
    const field = this.snippetForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.snippetForm.get(fieldName);
    if (field && field.errors && field.touched) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['maxlength']) return `${fieldName} cannot exceed ${field.errors['maxlength'].requiredLength} characters`;
    }
    return '';
  }
}
