import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Project, CreateProjectRequest } from '../models/content.models';

export interface ProjectResponse {
  id: number;
  title: string;
  description: string;
  repoUrl?: string;
  demoUrl?: string;
  technologies: string[];
  tags: string[];
  isPublic: boolean;
  isFeatured: boolean;
  createdAt: string | number[];
  updatedAt: string | number[];
  userId: number;
  username: string;
  userAvatarUrl?: string;
  views: number;
  likes: number;
  collaborators?: string[];
}

export interface PaginatedProjectResponse {
  content: ProjectResponse[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface UpdateProjectRequest {
  title: string;
  description: string;
  repoUrl?: string;
  demoUrl?: string;
  technologies: string[];
  tags: string[];
  isPublic: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  // Convert backend response to frontend model
  convertToProject(projectResponse: ProjectResponse): Project {
    return {
      id: projectResponse.id,
      name: projectResponse.title, // Backend uses 'title', frontend uses 'name'
      description: projectResponse.description,
      repositoryUrl: projectResponse.repoUrl,
      demoUrl: projectResponse.demoUrl,
      technologies: projectResponse.technologies,
      tags: projectResponse.tags,
      isPublic: projectResponse.isPublic,
      isFeatured: projectResponse.isFeatured,
      createdAt: this.parseDate(projectResponse.createdAt),
      updatedAt: this.parseDate(projectResponse.updatedAt),
      user: {
        id: projectResponse.userId,
        username: projectResponse.username,
        email: '', // Not provided in response
        avatar: projectResponse.userAvatarUrl || '/assets/default-avatar.png'
      },
      views: projectResponse.views,
      likes: projectResponse.likes,
      isLiked: false, // This would need to be determined by user state
      collaborators: projectResponse.collaborators || []
    };
  }

  // Get all projects with pagination
  getAllProjects(page: number = 0, size: number = 20): Observable<{ projects: Project[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedProjectResponse>(`${this.apiUrl}`, { params }).pipe(
      map(response => ({
        projects: response.content.map(p => this.convertToProject(p)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get project by ID
  getProjectById(id: number): Observable<ProjectResponse> {
    return this.http.get<ProjectResponse>(`${this.apiUrl}/${id}`);
  }

  // Get project details without incrementing views
  getProjectDetails(id: number): Observable<ProjectResponse> {
    return this.http.get<ProjectResponse>(`${this.apiUrl}/${id}/details`);
  }

  // Create new project
  createProject(project: CreateProjectRequest): Observable<ProjectResponse> {
    const requestBody = {
      title: project.name, // Convert name to title for backend
      description: project.description,
      repoUrl: project.repositoryUrl,
      demoUrl: project.demoUrl,
      technologies: project.technologies || [],
      tags: project.tags || [],
      isPublic: project.isPublic || true
    };
    return this.http.post<ProjectResponse>(`${this.apiUrl}`, requestBody);
  }

  // Update project
  updateProject(id: number, project: Partial<UpdateProjectRequest>): Observable<ProjectResponse> {
    return this.http.put<ProjectResponse>(`${this.apiUrl}/${id}`, project);
  }

  // Delete project
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Get user's projects
  getMyProjects(page: number = 0, size: number = 20): Observable<{ projects: Project[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedProjectResponse>(`${this.apiUrl}/my`, { params }).pipe(
      map(response => ({
        projects: response.content.map(p => this.convertToProject(p)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get user projects by user ID
  getUserProjects(userId: number, page: number = 0, size: number = 20, publicOnly: boolean = true): Observable<{ projects: Project[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('publicOnly', publicOnly.toString());

    return this.http.get<PaginatedProjectResponse>(`${this.apiUrl}/user/${userId}`, { params }).pipe(
      map(response => ({
        projects: response.content.map(p => this.convertToProject(p)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Get featured projects
  getFeaturedProjects(page: number = 0, size: number = 20): Observable<{ projects: Project[], total: number, totalPages: number }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedProjectResponse>(`${this.apiUrl}/featured`, { params }).pipe(
      map(response => ({
        projects: response.content.map(p => this.convertToProject(p)),
        total: response.totalElements,
        totalPages: response.totalPages
      }))
    );
  }

  // Search projects
  searchProjects(title: string): Observable<Project[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/search`, { params }).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Get projects by technology
  getProjectsByTechnology(technology: string): Observable<Project[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/technology/${technology}`).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Get projects by tag
  getProjectsByTag(tag: string): Observable<Project[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/tag/${tag}`).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Get trending projects
  getTrendingProjects(): Observable<Project[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/trending`).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Get popular projects
  getPopularProjects(): Observable<Project[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/popular`).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Get recent projects
  getRecentProjects(): Observable<Project[]> {
    return this.http.get<ProjectResponse[]>(`${this.apiUrl}/recent`).pipe(
      map(projects => projects.map(p => this.convertToProject(p)))
    );
  }

  // Like project
  likeProject(id: number): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(`${this.apiUrl}/${id}/like`, {});
  }

  // Unlike project
  unlikeProject(id: number): Observable<ProjectResponse> {
    return this.http.delete<ProjectResponse>(`${this.apiUrl}/${id}/like`);
  }

  // Get project count for user
  getUserProjectCount(userId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/${userId}/stats/count`);
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
