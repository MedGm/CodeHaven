import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BlogService, CreateBlogRequest } from '../../../core/services/blog.service';
import { SyntaxHighlightService } from '../../../core/services/syntax-highlight.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-blog-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './blog-create.component.html',
  styleUrl: './blog-create.component.css'
})
export class BlogCreateComponent implements OnInit {
  blogForm!: FormGroup;
  isLoading = false;
  errorMessage = '';
  activeEditorTab: 'write' | 'preview' = 'write';

  categories = [
    'Technology',
    'Programming',
    'Web Development',
    'Mobile Development',
    'Data Science',
    'AI/ML',
    'DevOps',
    'Tutorial',
    'Opinion',
    'News',
    'Career',
    'Other'
  ];

  constructor(
    private fb: FormBuilder,
    private blogService: BlogService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private syntaxHighlight: SyntaxHighlightService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.blogForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      excerpt: ['', [Validators.maxLength(500)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      coverImageUrl: [''],
      tags: [''],
      status: ['DRAFT'],
      isFeatured: [false],
      readingTime: [null]
    });
  }

  onSubmit(): void {
    if (this.blogForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const formValue = this.blogForm.value;
      const blogData: CreateBlogRequest = {
        title: formValue.title,
        content: formValue.content,
        excerpt: formValue.excerpt || '',
        coverImageUrl: formValue.coverImageUrl,
        tags: formValue.tags ? formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        status: formValue.status,
        isFeatured: formValue.isFeatured,
        readingTime: formValue.readingTime
      };

      this.blogService.createBlog(blogData).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/blogs', response.id]);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Failed to create blog post. Please try again.';
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/blogs']);
  }

  insertMarkdown(prefix: string, suffix: string = ''): void {
    const textarea = document.getElementById('content') as HTMLTextAreaElement;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = textarea.value.substring(start, end);
    const replacement = prefix + selectedText + suffix;

    const newValue = textarea.value.substring(0, start) + replacement + textarea.value.substring(end);
    
    // Update form control
    this.blogForm.get('content')?.setValue(newValue);
    
    // Set cursor position
    setTimeout(() => {
      textarea.focus();
      const newCursorPos = start + prefix.length + selectedText.length;
      textarea.setSelectionRange(newCursorPos, newCursorPos);
    });
  }

  getMarkdownPreview(): SafeHtml {
    const content = this.blogForm.get('content')?.value || '';
    
    // Enhanced markdown to HTML conversion with syntax highlighting
    let html = content
      // Headers
      .replace(/^### (.*$)/gim, '<h3 class="text-lg font-semibold text-gray-900 dark:text-white mt-6 mb-3">$1</h3>')
      .replace(/^## (.*$)/gim, '<h2 class="text-xl font-semibold text-gray-900 dark:text-white mt-8 mb-4">$1</h2>')
      .replace(/^# (.*$)/gim, '<h1 class="text-2xl font-bold text-gray-900 dark:text-white mt-10 mb-6">$1</h1>')
      
      // Bold and Italic
      .replace(/\*\*(.*?)\*\*/g, '<strong class="font-semibold">$1</strong>')
      .replace(/\*(.*?)\*/g, '<em class="italic">$1</em>')
      
      // Inline code
      .replace(/`(.*?)`/g, '<code class="px-1.5 py-0.5 bg-gray-100 dark:bg-gray-700 rounded text-sm font-mono">$1</code>')
      
      // Links
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" class="text-blue-600 dark:text-blue-400 hover:underline" target="_blank" rel="noopener noreferrer">$1</a>')
      
      // Line breaks
      .replace(/\n/g, '<br>');
    
    // Enhanced code blocks with syntax highlighting
    html = html.replace(/```(\w+)?\n([\s\S]*?)\n```/g, (match: string, language: string, code: string) => {
      const trimmedCode = code.trim();
      if (language && trimmedCode) {
        const result = this.syntaxHighlight.highlight(trimmedCode, language);
        const languageColor = this.syntaxHighlight.getLanguageColor(language);
        return `
          <div class="relative bg-gray-900 dark:bg-gray-800 rounded-lg mt-4 mb-4">
            <div class="flex items-center justify-between px-4 py-2 bg-gray-800 dark:bg-gray-700 rounded-t-lg">
              <div class="flex items-center space-x-2">
                <div class="w-3 h-3 rounded-full bg-red-500"></div>
                <div class="w-3 h-3 rounded-full bg-yellow-500"></div>
                <div class="w-3 h-3 rounded-full bg-green-500"></div>
              </div>
              <div class="flex items-center space-x-2">
                <div class="w-2 h-2 rounded-full" style="background-color: ${languageColor}"></div>
                <span class="text-xs text-gray-300 font-medium">${this.syntaxHighlight.getLanguageDisplayName(language)}</span>
              </div>
            </div>
            <pre class="p-4 overflow-x-auto rounded-b-lg"><code class="text-sm font-mono text-gray-100">${result.value}</code></pre>
          </div>
        `;
      } else {
        return `<pre class="bg-gray-100 dark:bg-gray-800 p-4 rounded-lg overflow-x-auto mt-4 mb-4"><code class="text-sm font-mono text-gray-800 dark:text-gray-200">${trimmedCode}</code></pre>`;
      }
    });
    
    // Lists
    html = html.replace(/^- (.*$)/gim, '<li class="ml-4">• $1</li>');
    html = html.replace(/(<li.*<\/li>)/s, '<ul class="space-y-1 mt-2 mb-4">$1</ul>');

    return this.sanitizer.bypassSecurityTrustHtml(html || '<p class="text-gray-500 dark:text-gray-400">Start writing to see preview...</p>');
  }
}
