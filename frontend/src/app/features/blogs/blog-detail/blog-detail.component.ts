import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { BlogService, BlogResponse } from '../../../core/services/blog.service';
import { AuthService } from '../../../core/services/auth.service';
import { Blog } from '../../../core/models/content.models';

@Component({
  selector: 'app-blog-detail',
  imports: [CommonModule, RouterModule],
  templateUrl: './blog-detail.component.html',
  styleUrl: './blog-detail.component.css'
})
export class BlogDetailComponent implements OnInit {
  blog: Blog | null = null;
  isLoading = true;
  errorMessage = '';
  canEdit = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private blogService: BlogService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.loadBlog();
  }

  loadBlog() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isLoading = true;
      this.blogService.getBlogById(+id).subscribe({
        next: (blogResponse) => {
          this.blog = this.convertToBlog(blogResponse);
          this.checkEditPermission();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading blog:', error);
          this.errorMessage = 'Failed to load blog post';
          this.isLoading = false;
        }
      });
    }
  }

  private convertToBlog(blogResponse: BlogResponse): Blog {
    return {
      id: blogResponse.id,
      title: blogResponse.title,
      content: blogResponse.content,
      excerpt: blogResponse.excerpt,
      summary: blogResponse.excerpt,
      category: '',
      tags: blogResponse.tags,
      status: blogResponse.status as 'DRAFT' | 'PUBLISHED' | 'ARCHIVED',
      isPublished: blogResponse.status === 'PUBLISHED',
      isFeatured: blogResponse.isFeatured,
      likes: blogResponse.likesCount,
      views: blogResponse.viewsCount,
      commentsCount: blogResponse.commentsCount,
      createdAt: this.parseDate(blogResponse.createdAt),
      updatedAt: this.parseDate(blogResponse.updatedAt),
      publishedAt: blogResponse.publishedAt ? this.parseDate(blogResponse.publishedAt) : undefined,
      user: {
        id: 0, // Author ID not provided in response, using placeholder
        username: blogResponse.authorUsername,
        email: '',
        avatar: '/assets/default-avatar.png',
        name: blogResponse.authorName || blogResponse.authorUsername
      },
      readingTime: blogResponse.readingTime || 5,
      coverImageUrl: blogResponse.coverImageUrl,
      isLikedByCurrentUser: blogResponse.isLikedByCurrentUser || false
    };
  }

  checkEditPermission() {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser && this.blog) {
      this.canEdit = currentUser.username === this.blog.user.username;
    }
  }

  likeBlog() {
    if (!this.blog?.id) return;

    if (this.blog.isLikedByCurrentUser) {
      this.blogService.unlikeBlog(this.blog.id).subscribe({
        next: (updatedBlogResponse) => {
          this.blog = this.convertToBlog(updatedBlogResponse);
        },
        error: (error) => {
          console.error('Error unliking blog:', error);
        }
      });
    } else {
      this.blogService.likeBlog(this.blog.id).subscribe({
        next: (updatedBlogResponse) => {
          this.blog = this.convertToBlog(updatedBlogResponse);
        },
        error: (error) => {
          console.error('Error liking blog:', error);
        }
      });
    }
  }

  deleteBlog() {
    if (!this.blog?.id) return;

    if (confirm('Are you sure you want to delete this blog post?')) {
      this.blogService.deleteBlog(this.blog.id).subscribe({
        next: () => {
          this.router.navigate(['/blogs']);
        },
        error: (error) => {
          console.error('Error deleting blog:', error);
        }
      });
    }
  }

  editBlog() {
    if (this.blog?.id) {
      this.router.navigate(['/blogs', this.blog.id, 'edit']);
    }
  }

  shareBlog() {
    if (this.blog) {
      const url = window.location.href;
      if (navigator.share) {
        navigator.share({
          title: this.blog.title,
          text: this.blog.excerpt,
          url: url
        });
      } else {
        // Fallback: copy to clipboard
        navigator.clipboard.writeText(url).then(() => {
          alert('Link copied to clipboard!');
        });
      }
    }
  }

  goBack() {
    this.router.navigate(['/blogs']);
  }

  private parseDate(dateInput: string | number[] | any): Date {
    // Handle null/undefined
    if (!dateInput) {
      return new Date();
    }

    // If it's already a valid date string, try parsing it
    if (typeof dateInput === 'string') {
      const parsed = new Date(dateInput);
      if (!isNaN(parsed.getTime())) {
        return parsed;
      }
    }

    // Handle LocalDateTime array format: [year, month, day, hour, minute, second, nanosecond]
    if (Array.isArray(dateInput)) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateInput;
      // Note: JavaScript Date months are 0-indexed, so subtract 1 from month
      return new Date(year, month - 1, day, hour, minute, second);
    }

    // Fallback to current date if parsing fails
    console.warn('Unable to parse date:', dateInput);
    return new Date();
  }
}
