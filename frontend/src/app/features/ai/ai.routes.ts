import { Routes } from '@angular/router';
import { AiDashboardComponent } from './ai-dashboard/ai-dashboard.component';
import { CodeReviewComponent } from './code-review/code-review.component';
import { BugFixComponent } from './bug-fix/bug-fix.component';
import { CodeGenerationComponent } from './code-generation/code-generation.component';

export const AI_ROUTES: Routes = [
  {
    path: '',
    component: AiDashboardComponent
  },
  {
    path: 'code-review',
    component: CodeReviewComponent
  },
  {
    path: 'bug-fix',
    component: BugFixComponent
  },
  {
    path: 'code-generation',
    component: CodeGenerationComponent
  },
  {
    path: 'optimize',
    loadComponent: () => import('./code-optimize/code-optimize.component').then(c => c.CodeOptimizeComponent)
  },
  {
    path: 'explain',
    loadComponent: () => import('./code-explain/code-explain.component').then(c => c.CodeExplainComponent)
  },
  {
    path: 'history',
    loadComponent: () => import('./ai-history/ai-history.component').then(c => c.AiHistoryComponent)
  }
];
