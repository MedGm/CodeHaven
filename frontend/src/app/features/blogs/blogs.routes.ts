import { Routes } from '@angular/router';

export const BLOGS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./blog-list/blog-list.component').then(c => c.BlogListComponent)
  },
  {
    path: 'create',
    loadComponent: () => import('./blog-create/blog-create.component').then(c => c.BlogCreateComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./blog-detail/blog-detail.component').then(c => c.BlogDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./blog-edit/blog-edit.component').then(c => c.BlogEditComponent)
  }
];
