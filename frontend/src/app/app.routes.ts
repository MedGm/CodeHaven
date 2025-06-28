import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Auth routes (without main layout)
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  // Main application routes (with layout)
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES)
      },
      {
        path: 'projects',
        loadChildren: () => import('./features/projects/projects.routes').then(m => m.PROJECTS_ROUTES)
      },
      {
        path: 'blogs',
        loadChildren: () => import('./features/blogs/blogs.routes').then(m => m.BLOGS_ROUTES)
      },
      {
        path: 'snippets',
        loadChildren: () => import('./features/snippets/snippets.routes').then(m => m.SNIPPETS_ROUTES)
      },
      {
        path: 'ai',
        loadChildren: () => import('./features/ai/ai.routes').then(m => m.AI_ROUTES)
      },
      {
        path: 'profile',
        loadChildren: () => import('./features/profile/profile.routes').then(m => m.PROFILE_ROUTES)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/auth/login'
  }
];
