# Project Management Module

This module provides comprehensive project management functionality for the CodeHaven backend application, enabling users to create, manage, and discover coding projects.

## Features Implemented

### 1. Project CRUD Operations
- **Create Project**: Create new projects with title, description, repository links, and technologies
- **Update Project**: Update project details (only by project owner)
- **Delete Project**: Delete projects (only by project owner)
- **Get Project**: Retrieve project details with automatic view count increment

### 2. Project Discovery & Search
- **List All Public Projects**: Paginated list of public projects
- **Search Projects**: Search projects by title
- **Filter by Technology**: Find projects using specific technologies
- **Filter by Tags**: Find projects with specific tags
- **User Projects**: Get projects by specific user (public only or all for owners)

### 3. Project Features
- **Featured Projects**: Admin-curated featured projects
- **Trending Projects**: Projects trending in the last 30 days based on engagement
- **Popular Projects**: Most liked projects
- **Recent Projects**: Recently created public projects
- **Like/Unlike**: Users can like and unlike projects
- **View Tracking**: Automatic view count increment when projects are accessed

### 4. Admin Features
- **Toggle Featured**: Admins can feature/unfeature projects
- **Project Statistics**: Get total project counts
- **Full Access**: Admins have access to all project management operations

## API Endpoints

### Public/Authenticated Endpoints
- `GET /api/projects` - Get all public projects (with pagination)
- `GET /api/projects/{id}` - Get project details (increments view count)
- `GET /api/projects/{id}/details` - Get project details (without view increment)
- `GET /api/projects/user/{userId}` - Get user's public projects
- `GET /api/projects/my` - Get current user's projects (all)
- `GET /api/projects/featured` - Get featured projects
- `GET /api/projects/trending` - Get trending projects
- `GET /api/projects/popular` - Get popular projects
- `GET /api/projects/recent` - Get recent projects
- `GET /api/projects/search?title={query}` - Search projects by title
- `GET /api/projects/technology/{tech}` - Get projects by technology
- `GET /api/projects/tag/{tag}` - Get projects by tag
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project (owner only)
- `DELETE /api/projects/{id}` - Delete project (owner only)
- `POST /api/projects/{id}/like` - Like project
- `DELETE /api/projects/{id}/like` - Unlike project
- `GET /api/projects/user/{userId}/stats/count` - Get user project count

### Admin Only Endpoints
- `POST /api/projects/{id}/toggle-featured` - Toggle featured status
- `GET /api/projects/stats/count` - Get total project count

## DTOs

### CreateProjectRequest
For creating new projects:
- `title` (required, max 200 chars) - Project title
- `description` (optional, max 1000 chars) - Project description
- `repoUrl` (optional) - Repository URL (GitHub, GitLab, etc.)
- `demoUrl` (optional) - Live demo URL
- `technologies` (optional) - List of technologies used
- `tags` (optional) - List of project tags
- `isPublic` (optional, default: true) - Project visibility

### UpdateProjectRequest
For updating existing projects:
- All fields from CreateProjectRequest but optional
- Only non-null fields are updated

### ProjectDto
Complete project information including:
- Basic info (id, title, description, URLs)
- Technical details (technologies, tags)
- Metadata (visibility, featured status, timestamps)
- User information (owner details)
- Statistics (views, likes)
- Additional fields (collaborators)

## Security Features

- **Owner Authorization**: Users can only modify their own projects
- **Role-based Access**: Admin features protected with `@PreAuthorize`
- **Input Validation**: Comprehensive validation on all DTOs
- **Current User Context**: Secure user identification with `@CurrentUser`

## Architecture

The module follows Clean Architecture principles:

### Domain Layer
- `Project` entity with comprehensive business fields
- `ProjectRepository` interface for data access patterns
- `ProjectService` interface for business operations

### Infrastructure Layer
- `ProjectRepositoryImpl` implementing the repository interface
- `ProjectServiceImpl` implementing the service interface
- `JpaProjectRepository` for database operations with custom queries

### Application Layer
- `ProjectManagementUseCase` orchestrating business operations
- DTOs for clean data transfer
- `ProjectMapper` for domain/DTO conversions

### Controller Layer
- `ProjectController` handling HTTP requests
- Input validation and response formatting
- Security annotations for access control

## Database Features

- **Advanced Queries**: Custom JPQL queries for complex operations
- **Pagination Support**: All list endpoints support pagination
- **Performance Optimized**: Efficient queries for trending and popular projects
- **Element Collections**: Support for technologies and tags lists
- **User Relations**: Proper foreign key relationships

## Project Statistics & Analytics

- **View Tracking**: Automatic view count increment
- **Like System**: Users can like/unlike projects
- **Trending Algorithm**: Based on combined likes and views in last 30 days
- **User Statistics**: Project counts per user
- **Global Statistics**: Total project counts for admin dashboards

## Project Visibility & Privacy

- **Public/Private Projects**: Users can control project visibility
- **Owner Access**: Owners can see all their projects
- **Public Access**: Non-owners only see public projects
- **Featured System**: Admin-curated featured projects for discovery

## Search & Discovery

- **Title Search**: Case-insensitive partial matching
- **Technology Filtering**: Find projects using specific technologies
- **Tag-based Discovery**: Browse projects by tags
- **Trending Discovery**: Algorithm-based trending projects
- **User-based Discovery**: Explore projects by specific users

## Error Handling

- **Authorization Checks**: Proper ownership validation
- **Input Validation**: Comprehensive field validation
- **Business Rules**: Enforcement of project visibility rules
- **Consistent Responses**: Standard HTTP status codes and error messages

## Next Steps

1. **File Upload**: Add support for project images/screenshots
2. **Collaboration**: Implement project collaboration features
3. **Project Comments**: Add commenting system for projects
4. **Project Stars**: Implement GitHub-style starring system
5. **Project Forks**: Add project forking capabilities
6. **Advanced Search**: Full-text search with Elasticsearch
7. **Project Categories**: Add project categorization
8. **Project Templates**: Create project templates for quick setup
9. **Integration APIs**: GitHub integration for automatic project sync
10. **Analytics Dashboard**: Detailed project analytics for users
