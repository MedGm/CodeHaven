import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { SnippetService } from '../../../core/services/snippet.service';
import { SyntaxHighlightService } from '../../../core/services/syntax-highlight.service';
import { Snippet } from '../../../core/models/content.models';
import { AuthService } from '../../../core/services/auth.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-snippet-detail',
  imports: [CommonModule, RouterModule],
  templateUrl: './snippet-detail.component.html',
  styleUrl: './snippet-detail.component.css'
})
export class SnippetDetailComponent implements OnInit {
  snippet: Snippet | null = null;
  isLoading = true;
  error: string | null = null;
  isOwner = false;
  currentUser: any = null;

  // Language color mapping (same as project list)
  private languageColors: { [key: string]: string } = {
    'JavaScript': '#f1e05a',
    'TypeScript': '#2b7489',
    'Python': '#3572A5',
    'Java': '#b07219',
    'C#': '#239120',
    'Go': '#00ADD8',
    'Rust': '#dea584',
    'PHP': '#4F5D95',
    'Ruby': '#701516',
    'Swift': '#ffac45',
    'Kotlin': '#F18E33',
    'Dart': '#00B4AB',
    'HTML': '#e34c26',
    'CSS': '#1572B6',
    'Vue': '#4FC08D',
    'React': '#61DAFB',
    'Angular': '#DD0031'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private snippetService: SnippetService,
    private authService: AuthService,
    private sanitizer: DomSanitizer,
    private syntaxHighlight: SyntaxHighlightService
  ) {}

  ngOnInit() {
    this.getCurrentUser();
    this.loadSnippet();
  }

  getCurrentUser() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  loadSnippet() {
    this.isLoading = true;
    this.error = null;

    const id = this.route.snapshot.paramMap.get('id');
    if (!id || isNaN(Number(id))) {
      this.error = 'Invalid snippet ID';
      this.isLoading = false;
      return;
    }

    this.snippetService.getSnippetById(Number(id)).subscribe({
      next: (response) => {
        this.snippet = this.snippetService.convertToSnippet(response);
        this.isOwner = this.currentUser && this.snippet.user.username === this.currentUser.username;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading snippet:', error);
        this.error = error.error?.message || 'Failed to load snippet';
        this.isLoading = false;
      }
    });
  }

  toggleLike() {
    if (!this.snippet || !this.snippet.id || !this.currentUser) {
      console.log('Cannot like: no snippet, ID, or user not logged in');
      return;
    }

    const action = this.snippet.isLikedByCurrentUser ? 
      this.snippetService.unlikeSnippet(this.snippet.id) :
      this.snippetService.likeSnippet(this.snippet.id);

    action.subscribe({
      next: (response) => {
        this.snippet = this.snippetService.convertToSnippet(response);
      },
      error: (error) => {
        console.error('Error updating like status:', error);
      }
    });
  }

  copyCode() {
    if (this.snippet) {
      navigator.clipboard.writeText(this.snippet.code).then(() => {
        console.log('Code copied to clipboard!');
      }).catch(err => {
        console.error('Failed to copy code:', err);
      });
    }
  }

  editSnippet() {
    if (this.snippet && this.snippet.id) {
      this.router.navigate(['/snippets', this.snippet.id, 'edit']);
    }
  }

  deleteSnippet() {
    if (!this.snippet || !this.snippet.id) return;

    if (confirm('Are you sure you want to delete this snippet?')) {
      this.snippetService.deleteSnippet(this.snippet.id).subscribe({
        next: () => {
          console.log('Snippet deleted successfully');
          this.router.navigate(['/snippets']);
        },
        error: (error) => {
          console.error('Error deleting snippet:', error);
          this.error = 'Failed to delete snippet';
        }
      });
    }
  }

  shareSnippet() {
    if (this.snippet) {
      const url = window.location.href;
      
      if (navigator.share) {
        navigator.share({
          title: this.snippet.title,
          text: this.snippet.description || '',
          url: url
        }).catch(err => {
          console.error('Error sharing:', err);
          this.fallbackShare(url);
        });
      } else {
        this.fallbackShare(url);
      }
    }
  }

  private fallbackShare(url: string) {
    navigator.clipboard.writeText(url).then(() => {
      console.log('URL copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy URL:', err);
    });
  }

  getFormattedDate(date: Date): string {
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  goBack() {
    this.router.navigate(['/snippets']);
  }

  getLanguageColor(language: string): string {
    return this.syntaxHighlight.getLanguageColor(language);
  }

  getCodeLineCount(): number {
    if (!this.snippet?.code) return 0;
    return this.snippet.code.split('\n').length;
  }

  getCodeLines(): string[] {
    if (!this.snippet?.code) return [];
    return this.snippet.code.split('\n');
  }

  getHighlightedCode(): SafeHtml {
    if (!this.snippet?.code) return '';
    
    // Use the advanced syntax highlighting service
    const language = this.snippet.language?.toLowerCase() || '';
    const result = this.syntaxHighlight.highlight(this.snippet.code, language);
    
    return this.sanitizer.bypassSecurityTrustHtml(result.value);
  }

  downloadSnippet(): void {
    if (!this.snippet) return;
    
    const extension = this.getFileExtension(this.snippet.language);
    const filename = `${this.snippet.title.replace(/[^a-z0-9]/gi, '_').toLowerCase()}${extension}`;
    
    const blob = new Blob([this.snippet.code], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }

  private getFileExtension(language: string): string {
    const extensions: { [key: string]: string } = {
      'JavaScript': '.js',
      'TypeScript': '.ts',
      'Python': '.py',
      'Java': '.java',
      'C#': '.cs',
      'Go': '.go',
      'Rust': '.rs',
      'PHP': '.php',
      'Ruby': '.rb',
      'Swift': '.swift',
      'Kotlin': '.kt',
      'Dart': '.dart',
      'HTML': '.html',
      'CSS': '.css',
      'SQL': '.sql',
      'JSON': '.json',
      'XML': '.xml',
      'YAML': '.yml'
    };
    
    return extensions[language] || '.txt';
  }
}
