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
    this.initializeNotifications();
  }

  private initializeNotifications() {
    console.log('Initializing notifications');
    // Set mock data first as fallback
    this.notificationsSubject.next(this.getMockNotifications());
    this.unreadCountSubject.next(this.getMockUnreadCount());
    
    // Try to load real data in the background (but don't let failures clear mock data)
    this.tryLoadRealData();
  }

  // Method to reinitialize notifications (call this after auth changes)
  public reinitialize() {
    console.log('Reinitializing notification service');
    this.initializeNotifications();
  }

  // Try to load real data but preserve mock data on failure
  private tryLoadRealData(): void {
    // Try to load real unread count
    this.getUnreadCount().subscribe({
      next: (response) => {
        console.log('Loaded real unread count:', response.count);
        this.unreadCountSubject.next(response.count);
      },
      error: (error) => {
        console.error('Error loading unread notification count, keeping mock data:', error);
        // Don't clear mock data, just log the error
      }
    });

    // Try to load real notifications
    this.getNotifications(0, 50).subscribe({
      next: (response) => {
        console.log('Loaded real notifications:', response.notifications.length);
        this.notificationsSubject.next(response.notifications);
        this.unreadCountSubject.next(response.unreadCount);
      },
      error: (error) => {
        console.error('Error loading notifications, keeping mock data:', error);
        // Don't clear mock data, just log the error
      }
    });
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
        console.log('Loaded real unread count:', response.count);
        this.unreadCountSubject.next(response.count);
      },
      error: (error) => {
        console.error('Error loading unread notification count:', error);
        // Only clear notifications if this is a deliberate API call, not during initialization
        // For initialization, use tryLoadRealData() instead
        if (error.status === 401 || error.status === 403) {
          console.log('Auth error in loadUnreadCount, clearing notifications');
          this.unreadCountSubject.next(0);
        } else {
          // Other error, use mock data
          console.log('Other error in loadUnreadCount, using mock data');
          this.unreadCountSubject.next(this.getMockUnreadCount());
        }
      }
    });
  }

  // Method to refresh unread count (call after marking as read)
  refreshUnreadCount(): void {
    // Use the safe method that doesn't clear notifications on error
    this.getUnreadCount().subscribe({
      next: (response) => {
        console.log('Refreshed unread count:', response.count);
        this.unreadCountSubject.next(response.count);
      },
      error: (error) => {
        console.error('Error refreshing unread count, keeping current value:', error);
        // Don't change the current count on error
      }
    });
  }

  // Load notifications for display
  loadNotifications(): void {
    this.getNotifications(0, 50).subscribe({
      next: (response) => {
        console.log('Loaded real notifications:', response.notifications.length);
        this.notificationsSubject.next(response.notifications);
        this.unreadCountSubject.next(response.unreadCount);
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        // Only clear notifications if this is a deliberate API call, not during initialization
        // For initialization, use tryLoadRealData() instead
        if (error.status === 401 || error.status === 403) {
          console.log('Auth error in loadNotifications, clearing notifications');
          this.notificationsSubject.next([]);
          this.unreadCountSubject.next(0);
        } else {
          // Other error, use mock data
          console.log('Other error in loadNotifications, using mock notifications');
          this.notificationsSubject.next(this.getMockNotifications());
          this.unreadCountSubject.next(this.getMockUnreadCount());
        }
      }
    });
  }

  // Ensure notifications are always available (either real or mock)
  ensureNotificationsLoaded(): void {
    const currentNotifications = this.notificationsSubject.value;
    if (currentNotifications.length === 0) {
      // Set mock data first
      this.notificationsSubject.next(this.getMockNotifications());
      this.unreadCountSubject.next(this.getMockUnreadCount());
    }
    // Try to load real notifications in background without affecting current state
    this.tryLoadRealData();
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

  // Debug method to check current state
  debugCurrentState(): void {
    console.log('Current notifications:', this.notificationsSubject.value.length);
    console.log('Current unread count:', this.unreadCountSubject.value);
    console.log('Mock notifications:', this.getMockNotifications().length);
  }
}
