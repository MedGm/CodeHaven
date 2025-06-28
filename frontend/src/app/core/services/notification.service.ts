import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  isRead: boolean;
  createdAt: string;
  actionUrl?: string;
  actionText?: string;
}

export interface NotificationResponse {
  notifications: Notification[];
  unreadCount: number;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  constructor(private http: HttpClient) {
    // Initialize with mock data immediately
    this.notificationsSubject.next(this.getMockNotifications());
    this.unreadCountSubject.next(this.getMockUnreadCount());
    
    // Try to load real data in background
    this.loadUnreadCount();
  }

  getNotifications(page = 0, size = 10): Observable<NotificationResponse> {
    return this.http.get<NotificationResponse>(`${environment.apiUrl}/notifications?page=${page}&size=${size}`);
  }

  getUnreadCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${environment.apiUrl}/notifications/unread-count`);
  }

  markAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/notifications/${notificationId}/read`, {});
  }

  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/notifications/mark-all-read`, {});
  }

  deleteNotification(notificationId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/notifications/${notificationId}`);
  }

  // Create a notification (for admin or system use)
  createNotification(notification: Omit<Notification, 'id' | 'createdAt' | 'isRead'>): Observable<Notification> {
    return this.http.post<Notification>(`${environment.apiUrl}/notifications`, notification);
  }

  private loadUnreadCount(): void {
    this.getUnreadCount().subscribe({
      next: (response) => {
        this.unreadCountSubject.next(response.count);
      },
      error: (error) => {
        console.error('Error loading unread notification count:', error);
        // Fall back to mock data if backend is not available
        this.unreadCountSubject.next(this.getMockUnreadCount());
      }
    });
  }

  // Method to refresh unread count (call after marking as read)
  refreshUnreadCount(): void {
    this.loadUnreadCount();
  }

  // Load notifications for display
  loadNotifications(): void {
    this.getNotifications(0, 50).subscribe({
      next: (response) => {
        this.notificationsSubject.next(response.notifications);
        this.unreadCountSubject.next(response.unreadCount);
      },
      error: (error) => {
        console.error('Error loading notifications, using mock data:', error);
        // Always ensure mock data is available if backend fails
        this.notificationsSubject.next(this.getMockNotifications());
        this.unreadCountSubject.next(this.getMockUnreadCount());
      }
    });
  }

  // Ensure notifications are always available (either real or mock)
  ensureNotificationsLoaded(): void {
    if (this.notificationsSubject.value.length === 0) {
      this.notificationsSubject.next(this.getMockNotifications());
      this.unreadCountSubject.next(this.getMockUnreadCount());
    }
    // Try to load real notifications in background
    this.loadNotifications();
  }

  // Mock notifications for development (remove in production)
  getMockNotifications(): Notification[] {
    return [
      {
        id: 1,
        title: 'Welcome to CodeHaven!',
        message: 'Start by creating your first project or exploring AI-powered features.',
        type: 'info',
        isRead: false,
        createdAt: new Date(Date.now() - 3600000).toISOString(), // 1 hour ago
        actionUrl: '/projects/create',
        actionText: 'Create Project'
      },
      {
        id: 2,
        title: 'New AI Feature Available',
        message: 'Try our enhanced code review feature with improved accuracy.',
        type: 'success',
        isRead: false,
        createdAt: new Date(Date.now() - 7200000).toISOString(), // 2 hours ago
        actionUrl: '/ai/code-review',
        actionText: 'Try It'
      },
      {
        id: 3,
        title: 'Code Snippet Shared',
        message: 'Your JavaScript utility function snippet was liked by 5 users.',
        type: 'info',
        isRead: true,
        createdAt: new Date(Date.now() - 86400000).toISOString(), // 1 day ago
        actionUrl: '/snippets',
        actionText: 'View Snippets'
      }
    ];
  }

  // Get unread count from mock data
  getMockUnreadCount(): number {
    return this.getMockNotifications().filter(n => !n.isRead).length;
  }
}
