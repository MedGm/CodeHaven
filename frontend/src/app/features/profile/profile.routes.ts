import { Routes } from '@angular/router';

export const PROFILE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./profile-view/profile-view.component').then(c => c.ProfileViewComponent)
  },
  {
    path: 'edit',
    loadComponent: () => import('./profile-edit/profile-edit.component').then(c => c.ProfileEditComponent)
  },
  {
    path: 'settings',
    loadComponent: () => import('./profile-settings/profile-settings.component').then(c => c.ProfileSettingsComponent)
  }
];
