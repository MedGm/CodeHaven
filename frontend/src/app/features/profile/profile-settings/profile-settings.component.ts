import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService, UserProfile, UserStats, UserPreferences } from '../../../core/services/user.service';

@Component({
  selector: 'app-profile-settings',
  imports: [CommonModule],
  templateUrl: './profile-settings.component.html',
  styleUrl: './profile-settings.component.css'
})
export class ProfileSettingsComponent implements OnInit, OnDestroy {
  isLoading = true;
  profile: UserProfile | null = null;
  stats: UserStats | null = null;
  preferences: UserPreferences | null = null;
  
  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadProfile();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadProfile() {
    this.isLoading = true;
    
    // Load profile data
    const profileSub = this.userService.getCurrentUserProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(profileSub);

    // Load user stats
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

    // Load user preferences
    const prefSub = this.userService.getUserPreferences().subscribe({
      next: (preferences) => {
        this.preferences = preferences;
        this.checkLoadingComplete();
      },
      error: (error) => {
        console.error('Error loading preferences:', error);
        this.checkLoadingComplete();
      }
    });
    this.subscriptions.push(prefSub);
  }

  private checkLoadingComplete() {
    if (this.profile && this.stats && this.preferences) {
      this.isLoading = false;
    }
  }
  editProfile() {
    this.router.navigate(['/profile/edit']);
  }

  toggleNotification(type: 'emailNotifications' | 'pushNotifications' | 'weeklyDigest' | 'marketingEmails') {
    if (this.preferences) {
      this.preferences[type] = !this.preferences[type];
      this.savePreferences();
    }
  }

  changeTheme(theme: 'light' | 'dark' | 'system') {
    if (this.preferences) {
      this.preferences.theme = theme;
      this.savePreferences();
      
      // Apply theme immediately
      if (theme === 'dark') {
        document.documentElement.classList.add('dark');
      } else if (theme === 'light') {
        document.documentElement.classList.remove('dark');
      } else {
        // System theme - check OS preference
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        if (prefersDark) {
          document.documentElement.classList.add('dark');
        } else {
          document.documentElement.classList.remove('dark');
        }
      }
      localStorage.setItem('theme', theme);
    }
  }

  changeProfileVisibility(visibility: 'public' | 'private') {
    if (this.preferences) {
      this.preferences.profileVisibility = visibility;
      this.savePreferences();
    }
  }

  private savePreferences() {
    if (this.preferences) {
      const prefSub = this.userService.updateUserPreferences(this.preferences).subscribe({
        next: (updatedPrefs) => {
          this.preferences = updatedPrefs;
          console.log('Preferences saved successfully');
        },
        error: (error) => {
          console.error('Error saving preferences:', error);
        }
      });
      this.subscriptions.push(prefSub);
    }
  }

  exportData() {
    // TODO: Implement data export when backend endpoint is available
    console.log('Exporting user data...');
    alert('Data export feature will be available soon!');
  }

  deleteAccount() {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      // TODO: Implement account deletion when backend endpoint is available
      console.log('Deleting account...');
      alert('Account deletion feature will be available soon!');
    }
  }

  logout() {
    if (confirm('Are you sure you want to log out?')) {
      // TODO: Integrate with AuthService when available
      console.log('Logging out...');
      localStorage.removeItem('auth_token');
      this.router.navigate(['/auth/login']);
    }
  }
}