import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BlogService } from '../../../core/services/blog.service';
import { AuthService } from '../../../core/services/auth.service';
import { Blog } from '../../../core/models/content.models';
import { User } from '../../../core/models/auth.models';

@Component({
  selector: 'app-blog-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './blog-list.component.html',
  styleUrl: './blog-list.component.css'
})
export class BlogListComponent implements OnInit {
  blogs: Blog[] = [];
  filteredBlogs: Blog[] = [];
  isLoading = false;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  currentUser: User | null = null;
  activeBlogMenu: number | null = null;
  
  // Filters and search
  searchTerm = '';
  selectedCategory = '';
  sortBy = 'created';

  constructor(
    private blogService: BlogService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.loadBlogs();
  }

  loadBlogs() {
    this.isLoading = true;
    this.blogService.getAllPublishedBlogs(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.blogs = response.blogs;
        this.filteredBlogs = [...this.blogs];
        this.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading blogs:', error);
        this.isLoading = false;
      }
    });
  }

  applyFilters() {
    this.filteredBlogs = this.blogs.filter(blog => {
      const matchesSearch = !this.searchTerm || 
        blog.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        blog.content.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesCategory = !this.selectedCategory || blog.category === this.selectedCategory;
      
      return matchesSearch && matchesCategory;
    });

    // Apply sorting
    this.filteredBlogs.sort((a, b) => {
      switch (this.sortBy) {
        case 'created':
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        case 'updated':
          return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
        case 'title':
          return a.title.localeCompare(b.title);
        default:
          return 0;
      }
    });
  }

  searchBlogs() {
    if (this.searchTerm.trim()) {
      this.blogService.searchBlogs(this.searchTerm).subscribe({
        next: (blogs) => {
          this.filteredBlogs = blogs;
        },
        error: (error) => {
          console.error('Error searching blogs:', error);
        }
      });
    } else {
      this.filteredBlogs = [...this.blogs];
    }
  }

  loadPage(page: number) {
    this.currentPage = page;
    this.loadBlogs();
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.loadPage(this.currentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.loadPage(this.currentPage - 1);
    }
  }

  deleteBlog(id: number) {
    if (confirm('Are you sure you want to delete this blog post?')) {
      this.blogService.deleteBlog(id).subscribe({
        next: () => {
          this.loadBlogs(); // Reload the list
        },
        error: (error) => {
          console.error('Error deleting blog:', error);
        }
      });
    }
  }

  isOwner(blog: Blog): boolean {
    return this.currentUser != null && 
           blog.user && 
           this.currentUser.username === blog.user.username;
  }

  toggleBlogMenu(blogId: number, event: Event): void {
    event.stopPropagation();
    this.activeBlogMenu = this.activeBlogMenu === blogId ? null : blogId;
  }

  shareBlog(blog: Blog): void {
    const url = `${window.location.origin}/blogs/${blog.id}`;
    
    if (navigator.share) {
      navigator.share({
        title: blog.title,
        text: blog.excerpt || blog.summary || '',
        url: url
      }).catch(err => {
        console.error('Error sharing:', err);
        this.fallbackShare(url);
      });
    } else {
      this.fallbackShare(url);
    }
  }

  private fallbackShare(url: string): void {
    navigator.clipboard.writeText(url).then(() => {
      console.log('URL copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy URL:', err);
    });
  }

  getEstimatedReadingTime(content: string): number {
    if (!content) return 0;
    
    const wordsPerMinute = 200;
    const wordCount = content.split(/\s+/).length;
    const readingTime = Math.ceil(wordCount / wordsPerMinute);
    
    return readingTime;
  }
}
