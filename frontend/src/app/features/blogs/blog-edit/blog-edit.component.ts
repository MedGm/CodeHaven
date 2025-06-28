import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BlogService, BlogResponse, UpdateBlogRequest } from '../../../core/services/blog.service';

@Component({
  selector: 'app-blog-edit',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './blog-edit.component.html',
  styleUrl: './blog-edit.component.css'
})
export class BlogEditComponent implements OnInit {
  editForm: FormGroup;
  blog: BlogResponse | null = null;
  isLoading = false;
  isSaving = false;
  blogId: number | null = null;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private blogService: BlogService
  ) {
    this.editForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      content: ['', [Validators.required, Validators.minLength(50)]],
      excerpt: ['', [Validators.maxLength(500)]],
      coverImageUrl: [''],
      tags: [''],
      status: ['DRAFT'],
      isFeatured: [false],
      readingTime: [null]
    });
  }

  ngOnInit() {
    this.blogId = +this.route.snapshot.paramMap.get('id')!;
    if (this.blogId) {
      this.loadBlog();
    }
  }

  loadBlog() {
    this.isLoading = true;
    this.blogService.getBlogById(this.blogId!).subscribe({
      next: (blog) => {
        this.blog = blog;
        this.populateForm();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading blog:', error);
        this.errorMessage = 'Failed to load blog post';
        this.isLoading = false;
      }
    });
  }

  populateForm() {
    if (this.blog) {
      this.editForm.patchValue({
        title: this.blog.title,
        content: this.blog.content,
        excerpt: this.blog.excerpt || '',
        coverImageUrl: this.blog.coverImageUrl || '',
        tags: this.blog.tags ? this.blog.tags.join(', ') : '',
        status: this.blog.status,
        isFeatured: this.blog.isFeatured,
        readingTime: this.blog.readingTime
      });
    }
  }

  onSubmit() {
    if (this.editForm.valid && this.blogId) {
      this.isSaving = true;
      this.errorMessage = '';

      const formValue = this.editForm.value;
      const updateData: UpdateBlogRequest = {
        title: formValue.title,
        content: formValue.content,
        excerpt: formValue.excerpt || '',
        coverImageUrl: formValue.coverImageUrl,
        tags: formValue.tags ? formValue.tags.split(',').map((tag: string) => tag.trim()).filter((tag: string) => tag) : [],
        status: formValue.status,
        isFeatured: formValue.isFeatured,
        readingTime: formValue.readingTime
      };

      this.blogService.updateBlog(this.blogId, updateData).subscribe({
        next: (updatedBlog) => {
          this.isSaving = false;
          this.router.navigate(['/blogs', updatedBlog.id]);
        },
        error: (error) => {
          this.isSaving = false;
          this.errorMessage = error.error?.message || 'Failed to update blog post. Please try again.';
        }
      });
    }
  }

  publishBlog() {
    if (this.blogId && this.blog?.status !== 'PUBLISHED') {
      this.blogService.publishBlog(this.blogId).subscribe({
        next: (updatedBlog) => {
          this.blog = updatedBlog;
          this.populateForm();
        },
        error: (error) => {
          console.error('Error publishing blog:', error);
        }
      });
    }
  }

  goBack() {
    if (this.blogId) {
      this.router.navigate(['/blogs', this.blogId]);
    } else {
      this.router.navigate(['/blogs']);
    }
  }
}
