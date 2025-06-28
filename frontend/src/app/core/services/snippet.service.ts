import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Snippet, SnippetCreate } from '../models/content.models';

export interface SnippetResponse {
  id: number;
  title: string;
  description?: string;
  code: string;
  language: string;
  tags: string[];
  isPublic: boolean;
  isGist?: boolean;
  gistUrl?: string;
  likesCount?: number;
  viewsCount?: number;
  isLikedByCurrentUser?: boolean;
  authorUsername: string;
  authorName?: string;
  createdAt: Date | string | number[];
  updatedAt: Date | string | number[];
}

export interface PaginatedSnippets {
  content: SnippetResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SnippetService {
  private readonly apiUrl = `${environment.apiUrl}/snippets`;

  constructor(private http: HttpClient) {}

  // Create a new snippet
  createSnippet(snippet: SnippetCreate): Observable<SnippetResponse> {
    return this.http.post<SnippetResponse>(this.apiUrl, snippet);
  }

  // Get snippet by ID
  getSnippetById(id: number): Observable<SnippetResponse> {
    return this.http.get<SnippetResponse>(`${this.apiUrl}/${id}`);
  }

  // Update snippet
  updateSnippet(id: number, snippet: Partial<SnippetCreate>): Observable<SnippetResponse> {
    return this.http.put<SnippetResponse>(`${this.apiUrl}/${id}`, snippet);
  }

  // Delete snippet
  deleteSnippet(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Get public snippets with pagination
  getPublicSnippets(page: number = 0, size: number = 20, sort: string = 'createdAt'): Observable<PaginatedSnippets> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    
    return this.http.get<PaginatedSnippets>(`${this.apiUrl}/public`, { params });
  }

  // Get user's snippets
  getUserSnippets(username: string, page: number = 0, size: number = 20): Observable<PaginatedSnippets> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PaginatedSnippets>(`${this.apiUrl}/users/${username}`, { params });
  }

  // Search snippets by title
  searchSnippetsByTitle(title: string): Observable<SnippetResponse[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<SnippetResponse[]>(`${this.apiUrl}/search`, { params });
  }

  // Get snippets by language
  getSnippetsByLanguage(language: string): Observable<SnippetResponse[]> {
    return this.http.get<SnippetResponse[]>(`${this.apiUrl}/language/${language}`);
  }

  // Get snippets by tag
  getSnippetsByTag(tag: string): Observable<SnippetResponse[]> {
    return this.http.get<SnippetResponse[]>(`${this.apiUrl}/tag/${tag}`);
  }

  // Get trending snippets
  getTrendingSnippets(): Observable<SnippetResponse[]> {
    return this.http.get<SnippetResponse[]>(`${this.apiUrl}/trending`);
  }

  // Like a snippet
  likeSnippet(id: number): Observable<SnippetResponse> {
    return this.http.post<SnippetResponse>(`${this.apiUrl}/${id}/like`, {});
  }

  // Unlike a snippet
  unlikeSnippet(id: number): Observable<SnippetResponse> {
    return this.http.delete<SnippetResponse>(`${this.apiUrl}/${id}/like`);
  }

  // Get all programming languages
  getAllLanguages(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/languages`);
  }

  // Helper method to convert backend response to frontend model
  convertToSnippet(response: SnippetResponse): Snippet {
    return {
      id: response.id,
      title: response.title,
      description: response.description,
      code: response.code,
      language: response.language,
      tags: response.tags || [],
      isPublic: response.isPublic,
      user: {
        username: response.authorUsername,
        name: response.authorName
      },
      createdAt: this.parseDate(response.createdAt),
      updatedAt: this.parseDate(response.updatedAt),
      views: response.viewsCount || 0,
      likes: response.likesCount || 0
    };
  }

  // Helper method to parse different date formats
  private parseDate(date: Date | string | number[]): Date {
    if (Array.isArray(date)) {
      // Handle LocalDateTime array from Java [year, month, day, hour, minute, second, nanosecond]
      return new Date(
        date[0], // year
        date[1] - 1, // month (subtract 1 for JS)
        date[2], // day
        date[3] || 0, // hour
        date[4] || 0, // minute
        date[5] || 0 // second
      );
    }
    return new Date(date);
  }
}
