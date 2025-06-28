import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { UserService, UserProfile, UpdateUserRequest } from '../../../core/services/user.service';
import { FileUploadService } from '../../../core/services/file-upload.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile-edit',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-edit.component.html',
  styleUrl: './profile-edit.component.css'
})
export class ProfileEditComponent implements OnInit, OnDestroy {
  profileForm: FormGroup;
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  currentProfile: UserProfile | null = null;
  
  private subscriptions: Subscription[] = [];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private fileUploadService: FileUploadService,
    private authService: AuthService
  ) {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      bio: ['', [Validators.maxLength(500)]],
      location: ['', [Validators.maxLength(100)]],
      website: ['', [Validators.pattern(/^https?:\/\/.+/)]],
      githubUsername: ['', [Validators.maxLength(39)]],
      twitterUsername: ['', [Validators.maxLength(15)]],
      linkedinUsername: ['', [Validators.maxLength(50)]]
    });
  }

  ngOnInit() {
    this.loadProfile();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadProfile() {
    this.isLoading = true;
    
    const profileSub = this.userService.getCurrentUserProfile().subscribe({
      next: (profile) => {
        this.currentProfile = profile;
        this.profileForm.patchValue({
          username: profile.username,
          email: profile.email,
          fullName: profile.fullName,
          bio: profile.bio,
          location: profile.location || '',
          website: profile.website || '',
          githubUsername: profile.githubUsername || '',
          twitterUsername: profile.twitterUsername || '',
          linkedinUsername: profile.linkedinUsername || ''
        });
        this.previewUrl = profile.avatar;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Failed to load profile data';
        this.isLoading = false;
      }
    });
    this.subscriptions.push(profileSub);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Validate file
      const validation = this.fileUploadService.validateImageFile(file);
      if (!validation.isValid) {
        this.errorMessage = validation.error || 'Invalid file';
        return;
      }

      this.selectedFile = file;
      
      // Create preview
      if (this.previewUrl) {
        this.fileUploadService.revokeImagePreview(this.previewUrl);
      }
      this.previewUrl = this.fileUploadService.createImagePreview(file);
      
      // Clear any previous error
      this.errorMessage = '';
    }
  }

  removeAvatar() {
    this.selectedFile = null;
    this.previewUrl = '/assets/default-avatar.png'; // Default avatar
    // Clear any error messages related to file upload
    if (this.errorMessage.includes('File upload')) {
      this.errorMessage = '';
    }
  }

  onSubmit() {
    if (this.profileForm.valid) {
      this.isSaving = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      // If there's a selected file, upload it first
      if (this.selectedFile) {
        this.uploadAvatarAndUpdateProfile();
      } else {
        this.updateProfileOnly();
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  private uploadAvatarAndUpdateProfile() {
    const uploadSub = this.fileUploadService.uploadAvatar(this.selectedFile!).subscribe({
      next: (response) => {
        if (response.success) {
          // Update the preview URL to the uploaded image
          this.previewUrl = response.url;
          
          // Now update the profile with the new avatar URL
          this.updateProfileOnly();
        } else {
          this.isSaving = false;
          this.errorMessage = response.message || 'Failed to upload avatar';
        }
      },
      error: (error) => {
        console.error('Avatar upload error:', error);
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Failed to upload avatar. Please try again.';
      }
    });
    this.subscriptions.push(uploadSub);
  }

  private updateProfileOnly() {
    const formData = this.profileForm.value;
    
    // Create update request object
    const updateRequest: UpdateUserRequest = {
      username: formData.username,
      email: formData.email,
      bio: formData.bio,
      githubUsername: formData.githubUsername || undefined
    };

    // Include avatar URL if available
    if (this.previewUrl && this.previewUrl !== '/assets/default-avatar.png') {
      updateRequest.avatarUrl = this.previewUrl;
    }

    const updateSub = this.userService.updateCurrentUserProfile(updateRequest).subscribe({
      next: (updatedProfile) => {
        this.isSaving = false;
        this.successMessage = 'Profile updated successfully!';
        
        // Update the auth service with the new profile data
        this.authService.updateCurrentUser({
          username: updatedProfile.username,
          email: updatedProfile.email,
          bio: updatedProfile.bio,
          avatarUrl: updatedProfile.avatar,
          githubUsername: updatedProfile.githubUsername
        });
        
        // Clear the selected file after successful upload
        if (this.selectedFile) {
          this.selectedFile = null;
        }
        
        // Navigate back to settings after a short delay
        setTimeout(() => {
          this.router.navigate(['/profile/settings']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error updating profile:', error);
        this.isSaving = false;
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
      }
    });
    this.subscriptions.push(updateSub);
  }

  onCancel() {
    this.router.navigate(['/profile/settings']);
  }

  private markFormGroupTouched() {
    Object.keys(this.profileForm.controls).forEach(key => {
      const control = this.profileForm.get(key);
      control?.markAsTouched();
    });
  }

  // Getter for easier access to form controls
  get username() { return this.profileForm.get('username'); }
  get email() { return this.profileForm.get('email'); }
  get fullName() { return this.profileForm.get('fullName'); }
  get bio() { return this.profileForm.get('bio'); }
  get location() { return this.profileForm.get('location'); }
  get website() { return this.profileForm.get('website'); }
  get githubUsername() { return this.profileForm.get('githubUsername'); }
  get twitterUsername() { return this.profileForm.get('twitterUsername'); }
  get linkedinUsername() { return this.profileForm.get('linkedinUsername'); }

  // Validation helper methods
  isFieldInvalid(fieldName: string): boolean {
    const field = this.profileForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.profileForm.get(fieldName);
    if (field?.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['maxlength']) return `${fieldName} is too long`;
      if (field.errors['email']) return 'Please enter a valid email address';
      if (field.errors['pattern']) return 'Please enter a valid URL starting with http:// or https://';
    }
    return '';
  }

  uploadAvatar(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file first';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    
    const uploadSub = this.fileUploadService.uploadAvatar(this.selectedFile).subscribe({
      next: (response) => {
        if (response.success) {
          // Update the current profile with new avatar URL
          if (this.currentProfile) {
            this.currentProfile.avatar = response.url;
          }
          
          // Update the preview URL
          this.previewUrl = response.url;
          
          // Update the auth service with the new avatar URL
          this.authService.updateCurrentUser({
            avatarUrl: response.url
          });
          
          this.successMessage = 'Avatar uploaded successfully!';
          
          // Clear the file selection
          this.selectedFile = null;
        } else {
          this.errorMessage = response.message || 'Upload failed';
        }
        this.isSaving = false;
      },
      error: (error) => {
        console.error('Avatar upload error:', error);
        this.errorMessage = error.error?.message || 'Failed to upload avatar. Please try again.';
        this.isSaving = false;
      }
    });
    
    this.subscriptions.push(uploadSub);
  }

  removeSelectedFile(): void {
    this.selectedFile = null;
    if (this.previewUrl) {
      this.fileUploadService.revokeImagePreview(this.previewUrl);
      this.previewUrl = null;
    }
    // Reset file input
    const fileInput = document.getElementById('avatar-upload') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }
}
