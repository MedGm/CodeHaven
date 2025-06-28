import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { Project } from '../../../core/models/content.models';

@Component({
  selector: 'app-project-detail',
  imports: [CommonModule, RouterModule],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css'
})
export class ProjectDetailComponent implements OnInit {
  project: Project | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && !isNaN(Number(id))) {
      this.loadProject(Number(id));
    } else {
      this.errorMessage = 'Invalid project ID';
      this.isLoading = false;
    }
  }

  private loadProject(id: number) {
    this.projectService.getProjectById(id).subscribe({
      next: (response) => {
        this.project = this.projectService.convertToProject(response);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading project:', error);
        this.errorMessage = error.error?.message || 'Failed to load project';
        this.isLoading = false;
      }
    });
  }

  getFormattedDate(date: Date): string {
    return this.projectService.getFormattedDate(date);
  }

  likeProject() {
    if (!this.project?.id) return;
    
    this.projectService.likeProject(this.project.id).subscribe({
      next: (response) => {
        this.project = this.projectService.convertToProject(response);
      },
      error: (error) => {
        console.error('Error liking project:', error);
      }
    });
  }

  unlikeProject() {
    if (!this.project?.id) return;
    
    this.projectService.unlikeProject(this.project.id).subscribe({
      next: (response) => {
        this.project = this.projectService.convertToProject(response);
      },
      error: (error) => {
        console.error('Error unliking project:', error);
      }
    });
  }
}
