import { Routes } from '@angular/router';

export const SNIPPETS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./snippet-list/snippet-list.component').then(c => c.SnippetListComponent)
  },
  {
    path: 'create',
    loadComponent: () => import('./snippet-create/snippet-create.component').then(c => c.SnippetCreateComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./snippet-detail/snippet-detail.component').then(c => c.SnippetDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./snippet-edit/snippet-edit.component').then(c => c.SnippetEditComponent)
  }
];
