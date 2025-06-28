import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProjectService, ProjectResponse, UpdateProjectRequest } from '../../../core/services/project.service';

@Component({
  selector: 'app-project-edit',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './project-edit.component.html',
  styleUrl: './project-edit.component.css'
})
export class ProjectEditComponent implements OnInit {
  editForm: FormGroup;
  project: ProjectResponse | null = null;
  isLoading = false;
  isSaving = false;
  projectId: number | null = null;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService
  ) {
    this.editForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      repoUrl: [''],
      demoUrl: [''],
      technologies: [''],
      tags: [''],
      isPublic: [true]
    });
  }

  ngOnInit() {
    this.projectId = +this.route.snapshot.paramMap.get('id')!;
    if (this.projectId) {
      this.loadProject();
    }
  }

  loadProject() {
    this.isLoading = true;
    this.projectService.getProjectById(this.projectId!).subscribe({
      next: (project) => {
        this.project = project;
        this.populateForm();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading project:', error);
        this.errorMessage = 'Failed to load project';
        this.isLoading = false;
      }
    });
  }

  populateForm() {
    if (this.project) {
      this.editForm.patchValue({
        title: this.project.title,
        description: this.project.description,
        repoUrl: this.project.repoUrl || '',
        demoUrl: this.project.demoUrl || '',
        technologies: this.project.technologies ? this.project.technologies.join(', ') : '',
        tags: this.project.tags ? this.project.tags.join(', ') : '',
        isPublic: this.project.isPublic
      });
    }
  }

  onSubmit() {
    if (this.editForm.valid && this.projectId) {
      this.isSaving = true;
      this.errorMessage = '';

      const formValue = this.editForm.value;
      const updateData: UpdateProjectRequest = {
        title: formValue.title,
        description: formValue.description,
        repoUrl: formValue.repoUrl || undefined,
        demoUrl: formValue.demoUrl || undefined,
        technologies: formValue.technologies ? 
          formValue.technologies.split(',').map((tech: string) => tech.trim()).filter((tech: string) => tech) : [],
        tags: formValue.tags ? 
          formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        isPublic: formValue.isPublic
      };

      this.projectService.updateProject(this.projectId, updateData).subscribe({
        next: (updatedProject) => {
          this.isSaving = false;
          this.router.navigate(['/projects', updatedProject.id]);
        },
        error: (error) => {
          this.isSaving = false;
          this.errorMessage = error.error?.message || 'Failed to update project. Please try again.';
        }
      });
    }
  }

  goBack() {
    if (this.projectId) {
      this.router.navigate(['/projects', this.projectId]);
    } else {
      this.router.navigate(['/projects']);
    }
  }
}
