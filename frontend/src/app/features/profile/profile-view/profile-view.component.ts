import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService, UserProfile, UserStats } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { BlogService } from '../../../core/services/blog.service';
import { ProjectService } from '../../../core/services/project.service';

interface ActivityItem {
  message: string;
  timestamp: Date;
}

@Component({
  selector: 'app-profile-view',
  imports: [CommonModule, RouterModule],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css'
})
export class ProfileViewComponent implements OnInit, OnDestroy {
  user: UserProfile | null = null;
  stats: UserStats | null = null;
  recentActivity: ActivityItem[] = [];
  isLoading = true;
  
  private subscriptions: Subscription[] = [];

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private blogService: BlogService,
    private projectService: ProjectService
  ) {}

  ngOnInit() {
    this.loadUserData();
    this.loadStats();
    this.loadRecentActivity();
    this.subscribeToUserUpdates();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private subscribeToUserUpdates() {
    // Listen for auth user updates (like avatar changes)
    const authUserSub = this.authService.currentUser$.subscribe(authUser => {
      if (authUser && this.user) {
        // Update the user profile with auth user data
        this.user = {
          ...this.user,
          avatar: authUser.avatarUrl || this.user.avatar,
          username: authUser.username,
          email: authUser.email,
          bio: authUser.bio || this.user.bio
        };
      }
    });
    this.subscriptions.push(authUserSub);
  }

  private loadUserData() {
    const userSub = this.userService.getCurrentUserProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading user data:', error);
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(userSub);
  }

  private loadStats() {
    const statsSub = this.userService.getUserStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading stats:', error);
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(statsSub);
  }

  private loadRecentActivity() {
    // TODO: Replace with actual API call when backend endpoint is available
    // For now, generate activity based on recent blogs and projects
    this.recentActivity = [
      {
        message: 'Profile loaded successfully',
        timestamp: new Date()
      }
    ];
    this.checkLoadingComplete();
  }

  private checkLoadingComplete() {
    if (this.user && this.stats && this.recentActivity) {
      this.isLoading = false;
    }
  }
}
