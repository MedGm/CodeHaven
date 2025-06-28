import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../../core/services/project.service';
import { SyntaxHighlightService } from '../../../core/services/syntax-highlight.service';
import { Project } from '../../../core/models/content.models';

@Component({
  selector: 'app-project-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.css'
})
export class ProjectListComponent implements OnInit {
  projects: Project[] = [];
  filteredProjects: Project[] = [];
  isLoading = false;
  
  // Filters and search
  searchTerm = '';
  selectedLanguage = '';
  sortBy = 'updated';
  
  // Pagination
  currentPage = 1;
  pageSize = 12;
  totalProjects = 0;
  totalPages = 1;
  
  // UI state
  activeProjectMenu: number | null = null;
  Math = Math; // Make Math available in template

  // Language color mapping (GitHub-style)
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
    private projectService: ProjectService,
    private router: Router,
    private syntaxHighlight: SyntaxHighlightService
  ) {}

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.isLoading = true;
    
    const page = this.currentPage - 1; // Backend uses 0-based pagination
    
    this.projectService.getAllProjects(page, this.pageSize).subscribe({
      next: (response) => {
        this.projects = response.projects;
        this.totalProjects = response.total;
        this.totalPages = response.totalPages;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.isLoading = false;
        // Could add error handling here
      }
    });
  }

  applyFilters() {
    let filtered = [...this.projects];

    // Apply search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(project =>
        project.name.toLowerCase().includes(term) ||
        project.description.toLowerCase().includes(term)
      );
    }

    // Apply technology filter (replacing language filter)
    if (this.selectedLanguage) {
      filtered = filtered.filter(project => 
        project.technologies && project.technologies.some(tech => 
          tech.toLowerCase().includes(this.selectedLanguage.toLowerCase())
        )
      );
    }

    // Apply sorting
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'name':
          return a.name.localeCompare(b.name);
        case 'created':
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        case 'updated':
        default:
          return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
      }
    });

    this.filteredProjects = filtered;
    this.totalPages = Math.ceil(filtered.length / this.pageSize);
  }

  onSearchChange() {
    this.currentPage = 1;
    this.applyFilters();
  }

  onFilterChange() {
    this.currentPage = 1;
    this.applyFilters();
  }

  onSortChange() {
    this.applyFilters();
  }

  navigateToProject(projectId: number) {
    this.router.navigate(['/projects', projectId]);
  }

  toggleProjectMenu(projectId: number, event: Event) {
    event.stopPropagation();
    this.activeProjectMenu = this.activeProjectMenu === projectId ? null : projectId;
  }

  cloneProject(project: Project) {
    // TODO: Implement project cloning
    console.log('Cloning project:', project.name);
  }

  deleteProject(projectId: number) {
    if (confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
      // TODO: Implement project deletion
      console.log('Deleting project:', projectId);
    }
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      // For pagination with actual API, you would reload data here
      // this.loadProjects();
    }
  }

  getLanguageColor(language: string): string {
    return this.syntaxHighlight.getLanguageColor(language);
  }

  openRepository(url: string): void {
    window.open(url, '_blank', 'noopener,noreferrer');
  }
}
