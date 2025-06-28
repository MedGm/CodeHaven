import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SnippetService } from '../../../core/services/snippet.service';
import { Snippet, SnippetCreate } from '../../../core/models/content.models';

@Component({
  selector: 'app-snippet-edit',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './snippet-edit.component.html',
  styleUrl: './snippet-edit.component.css'
})
export class SnippetEditComponent implements OnInit {
  snippetForm: FormGroup;
  isLoading = false;
  isEditMode = false;
  snippetId: number | null = null;
  tagInput = '';
  errorMessage = '';
  availableLanguages: string[] = [];

  // Default languages for code snippets
  defaultLanguages = [
    'javascript', 'typescript', 'python', 'java', 'csharp', 'cpp', 'c',
    'html', 'css', 'scss', 'php', 'ruby', 'go', 'rust', 'swift', 'kotlin',
    'dart', 'sql', 'shell', 'powershell', 'json', 'xml', 'yaml', 'markdown'
  ];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private snippetService: SnippetService
  ) {
    this.snippetForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      code: ['', [Validators.required]],
      language: ['javascript', Validators.required],
      tags: [[]],
      isPublic: [true]
    });
  }

  ngOnInit() {
    this.loadLanguages();
    this.route.params.subscribe(params => {
      if (params['id']) {
        const id = Number(params['id']);
        if (!isNaN(id)) {
          this.snippetId = id;
          this.isEditMode = true;
          this.loadSnippet();
        } else {
          this.errorMessage = 'Invalid snippet ID';
        }
      }
    });
  }

  loadLanguages() {
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

  loadSnippet() {
    if (!this.snippetId) return;

    this.isLoading = true;
    this.errorMessage = '';
    
    this.snippetService.getSnippetById(this.snippetId).subscribe({
      next: (response) => {
        const snippet = this.snippetService.convertToSnippet(response);
        this.snippetForm.patchValue({
          title: snippet.title,
          description: snippet.description || '',
          code: snippet.code,
          language: snippet.language,
          tags: snippet.tags,
          isPublic: snippet.isPublic
        });
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading snippet:', error);
        this.errorMessage = error.error?.message || 'Failed to load snippet';
        this.isLoading = false;
      }
    });
  }

  addTag() {
    const tag = this.tagInput.trim().toLowerCase();
    if (tag && !this.tags.includes(tag)) {
      const currentTags = this.snippetForm.get('tags')?.value || [];
      this.snippetForm.patchValue({
        tags: [...currentTags, tag]
      });
      this.tagInput = '';
    }
  }

  removeTag(tagToRemove: string) {
    const currentTags = this.snippetForm.get('tags')?.value || [];
    this.snippetForm.patchValue({
      tags: currentTags.filter((tag: string) => tag !== tagToRemove)
    });
  }

  onTagInputKeyup(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ',') {
      event.preventDefault();
      this.addTag();
    }
  }

  insertCodeTemplate() {
    const language = this.snippetForm.get('language')?.value;
    let template = '';

    switch (language) {
      case 'javascript':
      case 'typescript':
        template = `function example() {
  // Your code here
  console.log('Hello, World!');
}

example();`;
        break;
      case 'python':
        template = `def example():
    # Your code here
    print("Hello, World!")

if __name__ == "__main__":
    example()`;
        break;
      case 'java':
        template = `public class Example {
    public static void main(String[] args) {
        // Your code here
        System.out.println("Hello, World!");
    }
}`;
        break;
      case 'html':
        template = `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Example</title>
</head>
<body>
    <h1>Hello, World!</h1>
</body>
</html>`;
        break;
      default:
        template = '// Your code here\nconsole.log("Hello, World!");';
    }

    this.snippetForm.patchValue({ code: template });
  }

  onSubmit() {
    if (this.snippetForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      const formValue = this.snippetForm.value;
      const snippetData: Partial<SnippetCreate> = {
        title: formValue.title.trim(),
        description: formValue.description?.trim() || '',
        code: formValue.code,
        language: formValue.language,
        tags: formValue.tags || [],
        isPublic: formValue.isPublic
      };

      if (this.isEditMode && this.snippetId) {
        // Update existing snippet
        this.snippetService.updateSnippet(this.snippetId, snippetData).subscribe({
          next: (response) => {
            this.isLoading = false;
            this.router.navigate(['/snippets', this.snippetId]);
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = error.error?.message || 'Failed to update snippet';
            console.error('Error updating snippet:', error);
          }
        });
      } else {
        // Create new snippet
        this.snippetService.createSnippet(snippetData as SnippetCreate).subscribe({
          next: (response) => {
            this.isLoading = false;
            this.router.navigate(['/snippets', response.id]);
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = error.error?.message || 'Failed to create snippet';
            console.error('Error creating snippet:', error);
          }
        });
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel() {
    if (this.isEditMode && this.snippetId) {
      this.router.navigate(['/snippets', this.snippetId]);
    } else {
      this.router.navigate(['/snippets']);
    }
  }

  private markFormGroupTouched() {
    Object.keys(this.snippetForm.controls).forEach(key => {
      const control = this.snippetForm.get(key);
      control?.markAsTouched();
    });
  }

  // Getter for easier access to form controls
  get title() { return this.snippetForm.get('title'); }
  get description() { return this.snippetForm.get('description'); }
  get code() { return this.snippetForm.get('code'); }
  get language() { return this.snippetForm.get('language'); }
  get tags() { return this.snippetForm.get('tags')?.value || []; }

  // Validation helper methods
  isFieldInvalid(fieldName: string): boolean {
    const field = this.snippetForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.snippetForm.get(fieldName);
    if (field?.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['maxlength']) return `${fieldName} is too long`;
    }
    return '';
  }
}
