import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService, Notification } from '../../core/services/notification.service';
import { User } from '../../core/models/auth.models';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {
  currentUser: User | null = null;
  showUserMenu = false;
  showMobileMenu = false;
  showNotificationMenu = false;
  isDarkMode = false;
  notifications: Notification[] = [];
  unreadCount = 0;
  private authSubscription?: Subscription;
  private notificationSubscription?: Subscription;
  private unreadCountSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    public router: Router
  ) {}

  ngOnInit() {
    // Subscribe to current user
    this.authSubscription = this.authService.currentUser$.subscribe(
      user => {
        this.currentUser = user;
        if (user) {
          this.loadNotifications();
        }
      }
    );

    // Subscribe to notifications
    this.notificationSubscription = this.notificationService.notifications$.subscribe(
      notifications => this.notifications = notifications
    );

    // Subscribe to unread count
    this.unreadCountSubscription = this.notificationService.unreadCount$.subscribe(
      count => this.unreadCount = count
    );

    // Load theme preference
    this.isDarkMode = localStorage.getItem('theme') === 'dark';
    this.applyTheme();
  }

  ngOnDestroy() {
    this.authSubscription?.unsubscribe();
    this.notificationSubscription?.unsubscribe();
    this.unreadCountSubscription?.unsubscribe();
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    this.applyTheme();
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
  }

  private applyTheme() {
    if (this.isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
    this.showMobileMenu = false; // Close mobile menu if open
  }

  toggleMobileMenu() {
    this.showMobileMenu = !this.showMobileMenu;
    this.showUserMenu = false; // Close user menu if open
  }

  logout() {
    this.authService.logout();
    this.showUserMenu = false;
    this.router.navigate(['/auth/login']);
  }

  private loadNotifications() {
    // Ensure notifications are always loaded (mock as fallback)
    this.notificationService.ensureNotificationsLoaded();
  }

  toggleNotificationMenu() {
    this.showNotificationMenu = !this.showNotificationMenu;
    this.showUserMenu = false; // Close user menu if open
    this.showMobileMenu = false; // Close mobile menu if open
    
    // Load notifications when opening the menu
    if (this.showNotificationMenu) {
      this.notificationService.ensureNotificationsLoaded();
    }
  }

  markNotificationAsRead(notification: Notification) {
    if (!notification.isRead) {
      this.notificationService.markAsRead(notification.id).subscribe({
        next: () => {
          notification.isRead = true;
          this.notificationService.refreshUnreadCount();
          
          // Navigate if there's an action URL
          if (notification.actionUrl) {
            this.router.navigate([notification.actionUrl]);
            this.showNotificationMenu = false;
          }
        },
        error: (error) => {
          console.error('Error marking notification as read:', error);
          // For development, manually update
          notification.isRead = true;
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      });
    } else if (notification.actionUrl) {
      // If already read, just navigate
      this.router.navigate([notification.actionUrl]);
      this.showNotificationMenu = false;
    }
  }

  markAllNotificationsAsRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.notificationService.refreshUnreadCount();
      },
      error: (error) => {
        console.error('Error marking all notifications as read:', error);
        // For development, manually update
        this.notifications.forEach(n => n.isRead = true);
        this.unreadCount = 0;
      }
    });
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'success': return '✅';
      case 'warning': return '⚠️';
      case 'error': return '❌';
      default: return 'ℹ️';
    }
  }

  getRelativeTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    if (diffInSeconds < 60) return 'Just now';
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
    if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;
    
    return date.toLocaleDateString();
  }
}
