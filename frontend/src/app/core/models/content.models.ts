export interface Project {
  id?: number;
  name: string; // title in backend
  description: string;
  repositoryUrl?: string; // repoUrl in backend
  demoUrl?: string;
  technologies: string[];
  tags: string[];
  isPublic: boolean;
  isFeatured?: boolean;
  user: any; // Will be populated with User object
  createdAt: Date;
  updatedAt: Date;
  views?: number;
  likes?: number;
  isLiked?: boolean;
  collaborators?: string[];
  stars?: number; // GitHub-style star count
  forks?: number; // GitHub-style fork count
}

export interface CreateProjectRequest {
  name: string; // will be converted to title for backend
  description: string;
  repositoryUrl?: string; // will be converted to repoUrl for backend
  demoUrl?: string;
  technologies?: string[];
  tags?: string[];
  isPublic: boolean;
}

export interface Blog {
  id?: number;
  title: string;
  content: string;
  excerpt?: string;
  summary?: string;
  category?: string;
  tags: string[];
  status?: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  isPublished?: boolean;
  isFeatured?: boolean;
  likes?: number;
  views?: number;
  commentsCount?: number;
  user: any;
  createdAt: Date;
  updatedAt: Date;
  publishedAt?: Date;
  readingTime?: number;
  coverImageUrl?: string;
  isLikedByCurrentUser?: boolean;
}

export interface Snippet {
  id?: number;
  title: string;
  description?: string;
  code: string;
  language: string;
  tags: string[];
  isPublic: boolean;
  user: any;
  createdAt: Date;
  updatedAt: Date;
  views?: number;
  likes?: number;
  isGist?: boolean;
  gistUrl?: string;
  isLikedByCurrentUser?: boolean;
}

export interface SnippetCreate {
  title: string;
  description?: string;
  code: string;
  language: string;
  tags: string[];
  isPublic: boolean;
}
