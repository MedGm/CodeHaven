import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { BlogService } from '../../core/services/blog.service';
import { ProjectService } from '../../core/services/project.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit, OnDestroy {
  projectCount = 0;
  blogCount = 0;
  snippetCount = 0;
  isLoading = true;
  private loadingCounter = 0;
  private totalRequests = 3;
  
  private subscriptions: Subscription[] = [];

  constructor(
    private blogService: BlogService,
    private projectService: ProjectService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadCounts();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private loadCounts() {
    this.isLoading = true;
    
    // Load blog count
    const blogSub = this.blogService.getAllPublishedBlogs(0, 1).subscribe({
      next: (response: any) => {
        this.blogCount = response.total;
        this.checkLoadingComplete();
      },
      error: (error: any) => {
        console.error('Error loading blog count:', error);
        this.blogCount = 0;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(blogSub);

    // Load project count
    const projectSub = this.projectService.getAllProjects(0, 1).subscribe({
      next: (response: any) => {
        this.projectCount = response.total;
        this.checkLoadingComplete();
      },
      error: (error: any) => {
        console.error('Error loading project count:', error);
        this.projectCount = 0;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(projectSub);

    // Load user stats for snippet count
    const statsSub = this.userService.getUserStats().subscribe({
      next: (stats: any) => {
        this.snippetCount = stats.totalSnippets;
        this.checkLoadingComplete();
      },
      error: (error: any) => {
        console.error('Error loading user stats:', error);
        this.snippetCount = 0;
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(statsSub);
  }

  private checkLoadingComplete() {
    this.loadingCounter++;
    if (this.loadingCounter >= this.totalRequests) {
      this.isLoading = false;
    }
  }

  onImportProject() {
    // TODO: Implement project import functionality
    // For now, we could show a file input dialog or navigate to an import page
    console.log('Import project functionality to be implemented');
    
    // You could implement this by:
    // 1. Opening a file dialog to select project files
    // 2. Navigating to a dedicated import page
    // 3. Showing a modal with import options
    
    // Example: Navigate to import page (if you create one)
    // this.router.navigate(['/projects/import']);
  }
}
