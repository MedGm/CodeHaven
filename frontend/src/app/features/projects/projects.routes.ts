import { Routes } from '@angular/router';
import { ProjectListComponent } from './project-list/project-list.component';
import { ProjectDetailComponent } from './project-detail/project-detail.component';

export const PROJECTS_ROUTES: Routes = [
  {
    path: '',
    component: ProjectListComponent
  },
  {
    path: 'create',
    loadComponent: () => import('./project-create/project-create.component').then(c => c.ProjectCreateComponent)
  },
  {
    path: ':id',
    component: ProjectDetailComponent
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./project-edit/project-edit.component').then(c => c.ProjectEditComponent)
  }
];
