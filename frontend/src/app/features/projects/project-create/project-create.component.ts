import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { CreateProjectRequest } from '../../../core/models/content.models';

@Component({
  selector: 'app-project-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './project-create.component.html',
  styleUrl: './project-create.component.css'
})
export class ProjectCreateComponent implements OnInit {
  projectForm!: FormGroup;
  isLoading = false;
  errorMessage = '';

  availableTechnologies = [
    'JavaScript', 'TypeScript', 'Python', 'Java', 'C#', 'C++', 'Go', 'Rust',
    'PHP', 'Ruby', 'Swift', 'Kotlin', 'Dart', 'HTML', 'CSS', 'SCSS',
    'React', 'Angular', 'Vue.js', 'Node.js', 'Express', 'Spring Boot',
    'Django', 'Flask', 'ASP.NET', 'Laravel', 'Rails', 'MongoDB',
    'PostgreSQL', 'MySQL', 'Redis', 'Docker', 'Kubernetes', 'AWS',
    'Azure', 'GCP', 'Git', 'GitHub', 'GitLab', 'CI/CD'
  ];

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.projectForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      repoUrl: [''],
      demoUrl: [''],
      technologies: [''],
      tags: [''],
      isPublic: [true]
    });
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const formValue = this.projectForm.value;
      const projectData: CreateProjectRequest = {
        name: formValue.title,
        description: formValue.description,
        repositoryUrl: formValue.repoUrl || undefined,
        demoUrl: formValue.demoUrl || undefined,
        technologies: formValue.technologies ? 
          formValue.technologies.split(',').map((tech: string) => tech.trim()).filter((tech: string) => tech) : [],
        tags: formValue.tags ? 
          formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        isPublic: formValue.isPublic
      };

      this.projectService.createProject(projectData).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.router.navigate(['/projects', response.id]);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Failed to create project. Please try again.';
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/projects']);
  }
}
