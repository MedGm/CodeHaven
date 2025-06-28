import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SnippetService, SnippetResponse } from '../../../core/services/snippet.service';
import { Snippet } from '../../../core/models/content.models';

@Component({
  selector: 'app-snippet-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './snippet-list.component.html',
  styleUrl: './snippet-list.component.css'
})
export class SnippetListComponent implements OnInit {
  snippets: Snippet[] = [];
  filteredSnippets: Snippet[] = [];
  isLoading = false;
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;
  
  // Filters and search
  searchTerm = '';
  selectedLanguage = '';
  visibilityFilter = '';
  availableLanguages: string[] = [];

  constructor(private snippetService: SnippetService) {}

  ngOnInit() {
    this.loadSnippets();
    this.loadLanguages();
  }

  loadSnippets() {
    this.isLoading = true;
    
    this.snippetService.getPublicSnippets(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.snippets = response.content.map(snippet => this.snippetService.convertToSnippet(snippet));
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.filteredSnippets = [...this.snippets];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading snippets:', error);
        this.isLoading = false;
        // Fallback to empty array
        this.snippets = [];
        this.filteredSnippets = [];
      }
    });
  }

  loadLanguages() {
    this.snippetService.getAllLanguages().subscribe({
      next: (languages) => {
        this.availableLanguages = languages;
      },
      error: (error) => {
        console.error('Error loading languages:', error);
        // Fallback to common languages
        this.availableLanguages = ['javascript', 'typescript', 'python', 'java', 'cpp', 'csharp', 'go', 'rust'];
      }
    });
  }

  applyFilters() {
    let filtered = [...this.snippets];

    // Apply search filter
    if (this.searchTerm.trim()) {
      this.snippetService.searchSnippetsByTitle(this.searchTerm).subscribe({
        next: (response) => {
          this.filteredSnippets = response.map(snippet => this.snippetService.convertToSnippet(snippet));
        },
        error: (error) => {
          console.error('Error searching snippets:', error);
          // Fallback to local filtering
          this.localFilter();
        }
      });
      return;
    }

    // Apply language filter
    if (this.selectedLanguage) {
      this.snippetService.getSnippetsByLanguage(this.selectedLanguage).subscribe({
        next: (response) => {
          this.filteredSnippets = response.map(snippet => this.snippetService.convertToSnippet(snippet));
        },
        error: (error) => {
          console.error('Error filtering by language:', error);
          this.localFilter();
        }
      });
      return;
    }

    // If no server-side filtering, use local filtering
    this.localFilter();
  }

  private localFilter() {
    this.filteredSnippets = this.snippets.filter(snippet => {
      const matchesSearch = !this.searchTerm || 
        snippet.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        snippet.description?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        snippet.code.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesLanguage = !this.selectedLanguage || snippet.language === this.selectedLanguage;
      
      const matchesVisibility = !this.visibilityFilter || 
        (this.visibilityFilter === 'public' && snippet.isPublic) ||
        (this.visibilityFilter === 'private' && !snippet.isPublic);
      
      return matchesSearch && matchesLanguage && matchesVisibility;
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadSnippets();
  }

  getCodePreview(code: string): string {
    const lines = code.split('\n');
    return lines.slice(0, 8).join('\n') + (lines.length > 8 ? '\n...' : '');
  }

  copyToClipboard(code: string) {
    navigator.clipboard.writeText(code).then(() => {
      console.log('Code copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy code: ', err);
    });
  }

  deleteSnippet(id: number) {
    if (confirm('Are you sure you want to delete this snippet?')) {
      this.snippetService.deleteSnippet(id).subscribe({
        next: () => {
          this.snippets = this.snippets.filter(snippet => snippet.id !== id);
          this.applyFilters();
        },
        error: (error) => {
          console.error('Error deleting snippet:', error);
        }
      });
    }
  }

  likeSnippet(snippet: Snippet) {
    if (!snippet.id) return;
    
    const action = snippet.isLikedByCurrentUser ? 
      this.snippetService.unlikeSnippet(snippet.id) :
      this.snippetService.likeSnippet(snippet.id);

    action.subscribe({
      next: (response) => {
        const updatedSnippet = this.snippetService.convertToSnippet(response);
        const index = this.snippets.findIndex(s => s.id === snippet.id);
        if (index !== -1) {
          this.snippets[index] = updatedSnippet;
          this.applyFilters();
        }
      },
      error: (error) => {
        console.error('Error updating like status:', error);
      }
    });
  }
}
