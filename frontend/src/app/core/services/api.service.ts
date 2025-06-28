import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PaginatedResponse } from '../models/common.models';
import { Project, Blog, Snippet, CreateProjectRequest } from '../models/content.models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  // Generic HTTP methods
  get<T>(url: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(`${environment.apiUrl}${url}`, { params });
  }

  post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(`${environment.apiUrl}${url}`, body);
  }

  put<T>(url: string, body: any): Observable<T> {
    return this.http.put<T>(`${environment.apiUrl}${url}`, body);
  }

  delete<T>(url: string): Observable<T> {
    return this.http.delete<T>(`${environment.apiUrl}${url}`);
  }

  // Projects API
  getProjects(page = 0, size = 10): Observable<ApiResponse<PaginatedResponse<Project>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PaginatedResponse<Project>>>(`${environment.apiUrl}/projects`, { params });
  }

  getProject(id: number): Observable<ApiResponse<Project>> {
    return this.http.get<ApiResponse<Project>>(`${environment.apiUrl}/projects/${id}`);
  }

  createProject(project: CreateProjectRequest): Observable<ApiResponse<Project>> {
    return this.http.post<ApiResponse<Project>>(`${environment.apiUrl}/projects`, project);
  }

  updateProject(id: number, project: Partial<Project>): Observable<ApiResponse<Project>> {
    return this.http.put<ApiResponse<Project>>(`${environment.apiUrl}/projects/${id}`, project);
  }

  deleteProject(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiUrl}/projects/${id}`);
  }

  // Blogs API
  getBlogs(page = 0, size = 10): Observable<ApiResponse<PaginatedResponse<Blog>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PaginatedResponse<Blog>>>(`${environment.apiUrl}/blogs`, { params });
  }

  getBlog(id: number): Observable<ApiResponse<Blog>> {
    return this.http.get<ApiResponse<Blog>>(`${environment.apiUrl}/blogs/${id}`);
  }

  createBlog(blog: Partial<Blog>): Observable<ApiResponse<Blog>> {
    return this.http.post<ApiResponse<Blog>>(`${environment.apiUrl}/blogs`, blog);
  }

  updateBlog(id: number, blog: Partial<Blog>): Observable<ApiResponse<Blog>> {
    return this.http.put<ApiResponse<Blog>>(`${environment.apiUrl}/blogs/${id}`, blog);
  }

  // Snippets API
  getSnippets(page = 0, size = 10): Observable<ApiResponse<PaginatedResponse<Snippet>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PaginatedResponse<Snippet>>>(`${environment.apiUrl}/snippets`, { params });
  }

  getSnippet(id: number): Observable<ApiResponse<Snippet>> {
    return this.http.get<ApiResponse<Snippet>>(`${environment.apiUrl}/snippets/${id}`);
  }

  createSnippet(snippet: Partial<Snippet>): Observable<ApiResponse<Snippet>> {
    return this.http.post<ApiResponse<Snippet>>(`${environment.apiUrl}/snippets`, snippet);
  }
}
