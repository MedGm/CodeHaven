import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FileUploadResponse {
  success: boolean;
  message: string;
  url: string;
  fileName: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  constructor(private http: HttpClient) { }

  // Upload avatar file
  uploadAvatar(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<FileUploadResponse>(`${environment.apiUrl}/upload/avatar`, formData).pipe(
      map(response => ({
        ...response,
        url: this.getFullFileUrl(response.url)
      }))
    );
  }

  // Upload project image
  uploadProjectImage(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<FileUploadResponse>(`${environment.apiUrl}/upload/project-image`, formData).pipe(
      map(response => ({
        ...response,
        url: this.getFullFileUrl(response.url)
      }))
    );
  }

  // Convert relative URL to full URL
  private getFullFileUrl(relativePath: string): string {
    if (relativePath.startsWith('http')) {
      return relativePath; // Already a full URL
    }
    
    // Handle the case where backend returns /api/upload/files/...
    if (relativePath.startsWith('/api/upload/')) {
      // Extract the path after /api/
      const pathAfterApi = relativePath.substring(4); // Remove /api
      return `${environment.apiUrl}${pathAfterApi}`;
    }
    
    // Remove leading slash if present to avoid double slashes
    const cleanPath = relativePath.startsWith('/') ? relativePath.slice(1) : relativePath;
    return `${environment.apiUrl}/${cleanPath}`;
  }

  // Upload with progress tracking
  uploadAvatarWithProgress(file: File): Observable<HttpEvent<FileUploadResponse>> {
    const formData = new FormData();
    formData.append('file', file);
    
    const req = new HttpRequest('POST', `${environment.apiUrl}/upload/avatar`, formData, {
      reportProgress: true
    });
    
    return this.http.request<FileUploadResponse>(req);
  }

  // Validate file before upload
  validateImageFile(file: File): { isValid: boolean; error?: string } {
    // Check file size (10MB limit)
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      return { isValid: false, error: 'File size exceeds 10MB limit' };
    }

    // Check file type
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      return { isValid: false, error: 'Only image files (JPEG, PNG, GIF, WebP) are allowed' };
    }

    return { isValid: true };
  }

  // Create a preview URL for the image
  createImagePreview(file: File): string {
    return URL.createObjectURL(file);
  }

  // Clean up preview URL to prevent memory leaks
  revokeImagePreview(url: string): void {
    URL.revokeObjectURL(url);
  }

  // Resize image before upload (optional utility)
  resizeImage(file: File, maxWidth: number = 800, maxHeight: number = 800, quality: number = 0.8): Promise<File> {
    return new Promise((resolve) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const img = new Image();

      img.onload = () => {
        // Calculate new dimensions
        let { width, height } = img;
        
        if (width > height) {
          if (width > maxWidth) {
            height = (height * maxWidth) / width;
            width = maxWidth;
          }
        } else {
          if (height > maxHeight) {
            width = (width * maxHeight) / height;
            height = maxHeight;
          }
        }

        canvas.width = width;
        canvas.height = height;

        // Draw and compress
        ctx?.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob((blob) => {
          if (blob) {
            const resizedFile = new File([blob], file.name, {
              type: file.type,
              lastModified: Date.now()
            });
            resolve(resizedFile);
          } else {
            resolve(file); // fallback to original
          }
        }, file.type, quality);
      };

      img.src = URL.createObjectURL(file);
    });
  }
}
