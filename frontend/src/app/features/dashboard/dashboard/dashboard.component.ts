import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ProjectService } from '../../../core/services/project.service';
import { SnippetService } from '../../../core/services/snippet.service';
import { BlogService } from '../../../core/services/blog.service';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/auth.models';
import { Project } from '../../../core/models/content.models';

interface DashboardStats {
  projects: number;
  blogs: number;
  snippets: number;
}

interface ActivityItem {
  id: string;
  type: 'project' | 'blog' | 'snippet' | 'ai';
  title: string;
  description: string;
  timestamp: Date;
  status?: string;
  tags?: string[];
  resourceId?: string;
}

interface FeedFilter {
  key: string;
  label: string;
  activeClass: string;
  inactiveClass: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  stats: DashboardStats = {
    projects: 0,
    blogs: 0,
    snippets: 0
  };
  recentProjects: Project[] = [];
  recentActivity: ActivityItem[] = [];
  
  // Feed filter properties
  activeFilter: string = 'all';
  feedFilters: FeedFilter[] = [
    {
      key: 'all',
      label: 'All',
      activeClass: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400',
      inactiveClass: 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-600'
    },
    {
      key: 'project',
      label: 'Projects',
      activeClass: 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400',
      inactiveClass: 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-600'
    },
    {
      key: 'blog',
      label: 'Blogs',
      activeClass: 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400',
      inactiveClass: 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-600'
    },
    {
      key: 'snippet',
      label: 'Snippets',
      activeClass: 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400',
      inactiveClass: 'bg-gray-100 text-gray-600 dark:bg-gray-700 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-600'
    }
  ];

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private snippetService: SnippetService,
    private blogService: BlogService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    // Get current user
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Load dashboard data
    this.loadStats();
    this.loadRecentProjects();
    this.loadRecentActivity();
  }

  private loadStats() {
    // Load user stats from backend
    this.userService.getUserStats().subscribe({
      next: (stats) => {
        this.stats.projects = stats.totalProjects || 0;
        this.stats.blogs = stats.totalBlogs || 0;
        this.stats.snippets = stats.totalSnippets || 0;
      },
      error: (error) => {
        console.error('Error loading user stats:', error);
        this.stats = { projects: 0, blogs: 0, snippets: 0 };
      }
    });
  }

  private loadRecentProjects() {
    // Load user's recent projects (first 3)
    this.projectService.getMyProjects(0, 3).subscribe({
      next: (response) => {
        this.recentProjects = response.projects;
      },
      error: (error) => {
        console.error('Error loading recent projects:', error);
        this.recentProjects = [];
      }
    });
  }

  private loadRecentActivity() {
    // Load real activity data from recent user actions
    this.recentActivity = [];
    
    // Get recent projects (most recent first)
    this.projectService.getMyProjects(0, 3).subscribe({
      next: (response) => {
        response.projects.forEach(project => {
          this.recentActivity.push({
            id: `project-${project.id}`,
            type: 'project',
            title: `Updated project "${project.name}"`,
            description: `Modified ${project.technologies && project.technologies.length > 0 ? project.technologies.slice(0, 2).join(', ') : 'project'} project`,
            timestamp: project.updatedAt,
            status: 'Updated',
            tags: project.technologies || [],
            resourceId: project.id?.toString()
          });
        });
      },
      error: (error) => {
        console.error('Error loading recent projects for activity:', error);
      }
    });

    // Get recent blogs - only if we have a current user
    if (this.currentUser?.username) {
      this.blogService.getUserBlogsByUsername(this.currentUser.username, 0, 3).subscribe({
        next: (response) => {
          response.blogs.forEach(blog => {
            this.recentActivity.push({
              id: `blog-${blog.id}`,
              type: 'blog',
              title: `${blog.isPublished ? 'Published' : 'Updated'} blog post`,
              description: `"${blog.title}"`,
              timestamp: blog.updatedAt,
              status: blog.isPublished ? 'Published' : 'Draft',
              tags: blog.tags || [],
              resourceId: blog.id?.toString()
            });
          });
          
          // Sort by timestamp descending
          this.recentActivity.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
          
          // Keep only the 8 most recent
          this.recentActivity = this.recentActivity.slice(0, 8);
        },
        error: (error) => {
          console.error('Error loading recent blogs for activity:', error);
        }
      });
    }
  }

  // Computed property for filtered activity
  get filteredActivity(): ActivityItem[] {
    if (this.activeFilter === 'all') {
      return this.recentActivity;
    }
    return this.recentActivity.filter(activity => activity.type === this.activeFilter);
  }

  // Feed filter methods
  setActiveFilter(filter: string): void {
    this.activeFilter = filter;
  }

  // Navigation methods
  navigateToProject(projectId: number | string | undefined): void {
    if (projectId) {
      this.router.navigate(['/projects', projectId.toString()]);
    }
  }

  navigateToActivity(activity: ActivityItem): void {
    switch (activity.type) {
      case 'project':
        this.router.navigate(['/projects', activity.resourceId]);
        break;
      case 'blog':
        this.router.navigate(['/blogs', activity.resourceId]);
        break;
      case 'snippet':
        this.router.navigate(['/snippets', activity.resourceId]);
        break;
      case 'ai':
        this.router.navigate(['/ai']);
        break;
      default:
        break;
    }
  }

  // Activity icon methods
  getActivityIcon(type: string): string {
    switch (type) {
      case 'project':
        return 'M19 11H5m14-4h.01M6 15h1m6 0h1m6 0h.01M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z';
      case 'blog':
        return 'M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z';
      case 'snippet':
        return 'M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4';
      case 'ai':
        return 'M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z';
      default:
        return 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z';
    }
  }

  getActivityIconColor(type: string): string {
    switch (type) {
      case 'project':
        return 'text-blue-600 dark:text-blue-400';
      case 'blog':
        return 'text-green-600 dark:text-green-400';
      case 'snippet':
        return 'text-purple-600 dark:text-purple-400';
      case 'ai':
        return 'text-orange-600 dark:text-orange-400';
      default:
        return 'text-gray-600 dark:text-gray-400';
    }
  }

  getActivityIconBackground(type: string): string {
    switch (type) {
      case 'project':
        return 'bg-blue-100 dark:bg-blue-900/20';
      case 'blog':
        return 'bg-green-100 dark:bg-green-900/20';
      case 'snippet':
        return 'bg-purple-100 dark:bg-purple-900/20';
      case 'ai':
        return 'bg-orange-100 dark:bg-orange-900/20';
      default:
        return 'bg-gray-100 dark:bg-gray-700';
    }
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'published':
        return 'bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400';
      case 'draft':
        return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400';
      case 'updated':
        return 'bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400';
      case 'created':
        return 'bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400';
      default:
        return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-400';
    }
  }

  getEmptyStateMessage(): string {
    switch (this.activeFilter) {
      case 'project':
        return 'Start by creating your first project to see activity here.';
      case 'blog':
        return 'Write your first blog post to see activity here.';
      case 'snippet':
        return 'Save your first code snippet to see activity here.';
      case 'ai':
        return 'Use AI features to see activity here.';
      default:
        return 'Create projects, write blogs, or save snippets to see activity here.';
    }
  }

  // Track by function for performance
  trackActivity(index: number, activity: ActivityItem): string {
    return activity.id;
  }
}
