import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Blog } from '../models/content.models';

export interface BlogResponse {
  id: number;
  title: string;
  content: string;
  excerpt?: string;
  coverImageUrl?: string;
  tags: string[];
  likesCount: number;
  viewsCount: number;
  readingTime?: number;
  status: string; // 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
  isFeatured: boolean;
  isLikedByCurrentUser?: boolean;
  authorUsername: string;
  authorName?: string;
  commentsCount: number;
  createdAt: string | number[];
  updatedAt: string | number[];
  publishedAt?: string | number[];
}

export interface PaginatedBlogResponse {
  content: BlogResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface CreateBlogRequest {
  title: string;
  content: string;
  excerpt?: string;
  coverImageUrl?: string;
  tags: string[];
  readingTime?: number;
  isFeatured?: boolean;
  status: string; // 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
}

export interface UpdateBlogRequest {
  title: string;
  content: string;
  excerpt?: string;
  coverImageUrl?: string;
  tags: string[];
  readingTime?: number;
  isFeatured?: boolean;
  status: string; // 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'
}

@Injectable({
  providedIn: 'root'
})
export class BlogService {
  private apiUrl = `${environment.apiUrl}/blogs`;

  constructor(private http: HttpClient) {}

  // Convert backend response to frontend model
  convertToBlog(blogResponse: BlogResponse): Blog {
    return {
      id: blogResponse.id,
      title: blogResponse.title,
      content: blogResponse.content,
      excerpt: blogResponse.excerpt,
      summary: blogResponse.excerpt, // Use excerpt as summary
      category: '', // Not provided in backend response, empty for now
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
        id: 0, // Not directly provided in response
        username: blogResponse.authorUsername,
        email: '', // Not provided in response
        avatar: '/assets/default-avatar.png', // Default avatar
        name: blogResponse.authorName || blogResponse.authorUsername
      },
      readingTime: blogResponse.readingTime || 5,
      coverImageUrl: blogResponse.coverImageUrl,
      isLikedByCurrentUser: blogResponse.isLikedByCurrentUser || false
    };
  }

  // Get all published blogs with pagination
  getAllPublishedBlogs(page: number = 0, size: number = 20): Observable<{ blogs: Blog[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedBlogResponse>(`${this.apiUrl}/published`, { params }).pipe(
      map(response => ({
        blogs: response.content.map(b => this.convertToBlog(b)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get blog by ID
  getBlogById(id: number): Observable<BlogResponse> {
    return this.http.get<BlogResponse>(`${this.apiUrl}/${id}`);
  }

  // Get blog details without incrementing views
  getBlogDetails(id: number): Observable<BlogResponse> {
    return this.http.get<BlogResponse>(`${this.apiUrl}/${id}/details`);
  }

  // Create new blog
  createBlog(blog: CreateBlogRequest): Observable<BlogResponse> {
    return this.http.post<BlogResponse>(`${this.apiUrl}`, blog);
  }

  // Update blog
  updateBlog(id: number, blog: UpdateBlogRequest): Observable<BlogResponse> {
    return this.http.put<BlogResponse>(`${this.apiUrl}/${id}`, blog);
  }

  // Delete blog
  deleteBlog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Get user's blogs
  getMyBlogs(page: number = 0, size: number = 20): Observable<{ blogs: Blog[], total: number, totalPages: number }> {
    // TODO: Get current username from auth service or pass it as parameter
    // For now, return empty result as we need the current user's username
    return new Observable(observer => {
      observer.next({
        blogs: [],
        total: 0,
        totalPages: 0
      });
      observer.complete();
    });
  }

  // Get user blogs by user ID
  getUserBlogs(userId: number, page: number = 0, size: number = 20): Observable<{ blogs: Blog[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedBlogResponse>(`${this.apiUrl}/user/${userId}`, { params }).pipe(
      map(response => ({
        blogs: response.content.map(b => this.convertToBlog(b)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get user blogs by username (matches backend endpoint /users/{username})
  getUserBlogsByUsername(username: string, page: number = 0, size: number = 20): Observable<{ blogs: Blog[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedBlogResponse>(`${this.apiUrl}/users/${username}`, { params }).pipe(
      map(response => ({
        blogs: response.content.map(b => this.convertToBlog(b)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get featured blogs
  getFeaturedBlogs(page: number = 0, size: number = 20): Observable<{ blogs: Blog[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedBlogResponse>(`${this.apiUrl}/featured`, { params }).pipe(
      map(response => ({
        blogs: response.content.map(b => this.convertToBlog(b)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Search blogs
  searchBlogs(title: string): Observable<Blog[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/search`, { params }).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Get blogs by category
  getBlogsByCategory(category: string): Observable<Blog[]> {
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/category/${category}`).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Get blogs by tag
  getBlogsByTag(tag: string): Observable<Blog[]> {
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/tag/${tag}`).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Get recent blogs
  getRecentBlogs(): Observable<Blog[]> {
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/recent`).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Get popular blogs
  getPopularBlogs(): Observable<Blog[]> {
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/popular`).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Get trending blogs
  getTrendingBlogs(): Observable<Blog[]> {
    return this.http.get<BlogResponse[]>(`${this.apiUrl}/trending`).pipe(
      map(blogs => blogs.map(b => this.convertToBlog(b)))
    );
  }

  // Like blog
  likeBlog(id: number): Observable<BlogResponse> {
    return this.http.post<BlogResponse>(`${this.apiUrl}/${id}/like`, {});
  }

  // Unlike blog
  unlikeBlog(id: number): Observable<BlogResponse> {
    return this.http.delete<BlogResponse>(`${this.apiUrl}/${id}/like`);
  }

  // Publish blog
  publishBlog(id: number): Observable<BlogResponse> {
    return this.http.put<BlogResponse>(`${this.apiUrl}/${id}/publish`, {});
  }

  // Unpublish blog
  unpublishBlog(id: number): Observable<BlogResponse> {
    return this.http.put<BlogResponse>(`${this.apiUrl}/${id}/unpublish`, {});
  }

  // Get blog count for user
  getUserBlogCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/${userId}/stats/count`);
  }

  // Get all categories
  getAllCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }

  // Helper method to format dates
  getFormattedDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  // Parse date from various formats
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
