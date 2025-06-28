# User Management Module

This module provides comprehensive user management functionality for the CodeHaven backend application.

## Features Implemented

### 1. User Profile Management
- **Get User Profile**: Retrieve user profile information
- **Update User Profile**: Update user bio, avatar, GitHub username, etc.
- **Change Password**: Secure password change with current password verification

### 2. User Administration (Admin Only)
- **List All Users**: Paginated list of all users in the system
- **List Active Users**: Paginated list of active users only
- **Update User Role**: Change user roles (USER/ADMIN)
- **Deactivate User**: Deactivate a user account
- **Delete User**: Permanently delete a user account
- **User Statistics**: Get total user count

### 3. User Search & Discovery
- **Search Users**: Search users by username (partial matching)
- **Top Contributors**: Get list of top contributing users based on projects/blogs

## API Endpoints

### Public/Authenticated Endpoints
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update current user profile
- `POST /api/users/me/change-password` - Change current user password
- `DELETE /api/users/me/deactivate` - Deactivate current user account
- `GET /api/users/{userId}` - Get specific user profile
- `GET /api/users/search?username={query}` - Search users by username
- `GET /api/users/top-contributors` - Get top contributors

### Admin Only Endpoints
- `GET /api/users` - Get all users (with pagination)
- `PUT /api/users/{userId}` - Update any user profile
- `DELETE /api/users/{userId}` - Delete user account
- `POST /api/users/{userId}/deactivate` - Deactivate user account
- `PUT /api/users/{userId}/role?role={ROLE}` - Update user role
- `GET /api/users/stats/count` - Get total user count

## DTOs

### UserProfileDto
Contains complete user profile information including:
- Basic info (id, username, email)
- Profile details (bio, avatar, GitHub username)
- Technical information (tech stack, role)
- Timestamps (joined date, last updated)
- Status (active/inactive)

### UpdateUserRequest
For updating user profile information:
- Username (optional, with uniqueness validation)
- Email (optional, with uniqueness and format validation)
- Bio (optional, max 500 characters)
- Avatar URL (optional)
- GitHub username (optional)

### ChangePasswordRequest
For secure password changes:
- Current password (required for verification)
- New password (required, min 6 characters)
- Confirm password (required, must match new password)

## Security Features

- **Role-based Access Control**: Admin endpoints protected with `@PreAuthorize`
- **User Authentication**: All endpoints require authentication
- **Password Security**: Passwords are hashed using BCrypt
- **Input Validation**: All DTOs have comprehensive validation
- **Current User Context**: Uses `@CurrentUser` annotation for secure user identification

## Architecture

The module follows Clean Architecture principles:

### Domain Layer
- `User` entity with all business fields
- `UserRepository` interface for data access
- `UserService` interface for business logic

### Infrastructure Layer
- `UserRepositoryImpl` implementing the repository interface
- `UserServiceImpl` implementing the service interface
- `JpaUserRepository` for database operations

### Application Layer
- `UserManagementUseCase` orchestrating business operations
- DTOs for data transfer
- `UserMapper` for domain/DTO conversions

### Controller Layer
- `UserController` handling HTTP requests
- Input validation and response formatting
- Security annotations for access control

## Database Integration

- Uses JPA/Hibernate for database operations
- Supports pagination for large datasets
- Optimized queries for user search and statistics
- Cascading operations for related entities

## Error Handling

- Comprehensive validation with meaningful error messages
- Business rule enforcement (unique username/email)
- Global exception handling for consistent error responses
- Proper HTTP status codes for different scenarios

## Next Steps

1. Add profile picture upload functionality
2. Implement email verification for email changes
3. Add user activity tracking
4. Enhance search with additional filters
5. Add user preferences and settings
6. Implement user following/followers functionality
