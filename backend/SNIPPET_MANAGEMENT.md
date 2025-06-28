# Snippet Management System

## Overview
The Snippet Management system allows users to create, update, delete, and manage code snippets. It provides features for organizing snippets by language and tags, viewing analytics, and social features like liking.

## Architecture
The implementation follows Clean Architecture principles with clear separation of concerns:

- **Domain Layer**: `Snippet` entity and `SnippetService` interface
- **Infrastructure Layer**: `JpaSnippetRepository`, `SnippetRepositoryImpl`, and `SnippetServiceImpl`
- **Application Layer**: DTOs, mappers, and use cases
- **Controller Layer**: REST API endpoints

## API Endpoints

### Core Snippet Operations

#### Create Snippet
```http
POST /api/snippets
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "String (required, max 255 chars)",
  "description": "String (optional, max 1000 chars)",
  "code": "String (required)",
  "language": "String (required, max 50 chars)",
  "tags": ["String array (optional)"],
  "isPublic": "Boolean (required)",
  "isGist": "Boolean (optional)",
  "gistUrl": "String (optional)"
}
```

#### Update Snippet
```http
PUT /api/snippets/{snippetId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "String (required, max 255 chars)",
  "description": "String (optional, max 1000 chars)",
  "code": "String (required)",
  "language": "String (required, max 50 chars)",
  "tags": ["String array (optional)"],
  "isPublic": "Boolean (required)",
  "isGist": "Boolean (optional)",
  "gistUrl": "String (optional)"
}
```

#### Get Snippet by ID
```http
GET /api/snippets/{snippetId}
```

#### Delete Snippet
```http
DELETE /api/snippets/{snippetId}
Authorization: Bearer {token}
```

### Snippet Discovery

#### Get Public Snippets
```http
GET /api/snippets/public?page=0&size=20&sort=createdAt
```

#### Get User's Snippets
```http
GET /api/snippets/users/{username}?page=0&size=20&sort=createdAt
```

#### Search Snippets by Title
```http
GET /api/snippets/search?title={searchTerm}
```

#### Get Snippets by Language
```http
GET /api/snippets/language/{language}
```

#### Get Snippets by Tag
```http
GET /api/snippets/tag/{tag}
```

#### Get Trending Snippets
```http
GET /api/snippets/trending
```

### Social Features

#### Like Snippet
```http
POST /api/snippets/{snippetId}/like
Authorization: Bearer {token}
```

#### Unlike Snippet
```http
DELETE /api/snippets/{snippetId}/like
Authorization: Bearer {token}
```

### Utility Endpoints

#### Get All Languages
```http
GET /api/snippets/languages
```

## Data Models

### CreateSnippetRequest
```json
{
  "title": "String (required, max 255 chars)",
  "description": "String (optional, max 1000 chars)",
  "code": "String (required)",
  "language": "String (required, max 50 chars)",
  "tags": ["String array (optional)"],
  "isPublic": "Boolean (required)",
  "isGist": "Boolean (optional)",
  "gistUrl": "String (optional)"
}
```

### UpdateSnippetRequest
```json
{
  "title": "String (required, max 255 chars)",
  "description": "String (optional, max 1000 chars)",
  "code": "String (required)",
  "language": "String (required, max 50 chars)",
  "tags": ["String array (optional)"],
  "isPublic": "Boolean (required)",
  "isGist": "Boolean (optional)",
  "gistUrl": "String (optional)"
}
```

### SnippetDto
```json
{
  "id": "Long",
  "title": "String",
  "description": "String",
  "code": "String",
  "language": "String",
  "tags": ["String array"],
  "isPublic": "Boolean",
  "isGist": "Boolean",
  "gistUrl": "String",
  "likesCount": "Integer",
  "viewsCount": "Integer",
  "isLikedByCurrentUser": "Boolean",
  "authorUsername": "String",
  "authorName": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## Security

### Authentication
- Most endpoints require JWT authentication via Bearer token
- Public endpoints: Get snippet by ID, Get public snippets, Search, Get by language/tag, Get trending

### Authorization
- Users can only update/delete their own snippets
- Private snippets can only be viewed by their owners
- Users can only like public snippets or their own snippets

## Features

### Core Features
- ✅ Create, read, update, delete snippets
- ✅ Public/private visibility control
- ✅ Tagging system for organization
- ✅ Language-based categorization
- ✅ GitHub Gist integration support

### Discovery Features
- ✅ Search by title
- ✅ Filter by programming language
- ✅ Filter by tags
- ✅ Trending snippets
- ✅ User-specific snippet listings
- ✅ Pagination support

### Social Features
- ✅ Like/unlike snippets
- ✅ View count tracking
- ✅ Like count tracking
- ✅ Author information display

### Analytics
- ✅ View count per snippet
- ✅ Like count per snippet
- ✅ Popular snippets ranking
- ✅ Language usage statistics

## Database Schema

### snippets table
- `id` (Primary Key)
- `title` (VARCHAR, NOT NULL)
- `description` (TEXT)
- `code` (TEXT, NOT NULL)
- `language` (VARCHAR, NOT NULL)
- `views` (BIGINT, DEFAULT 0)
- `likes` (BIGINT, DEFAULT 0)
- `is_public` (BOOLEAN, DEFAULT true)
- `is_gist` (BOOLEAN, DEFAULT false)
- `gist_url` (VARCHAR)
- `user_id` (Foreign Key to users table)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### snippet_tags table
- `snippet_id` (Foreign Key)
- `tag` (VARCHAR)

## Service Layer Methods

### SnippetService Interface
- `createSnippet(Snippet snippet)`: Creates a new snippet
- `updateSnippet(Long snippetId, Snippet snippetUpdate)`: Updates an existing snippet
- `findById(Long snippetId)`: Finds snippet by ID
- `findPublicSnippets(Pageable pageable)`: Gets paginated public snippets
- `findSnippetsByUser(User user, Pageable pageable)`: Gets user's snippets
- `findPublicSnippetsByUser(User user, Pageable pageable)`: Gets user's public snippets
- `searchSnippetsByTitle(String title)`: Searches snippets by title
- `findSnippetsByLanguage(String language)`: Finds snippets by language
- `findSnippetsByTag(String tag)`: Finds snippets by tag
- `getTrendingSnippets()`: Gets trending snippets
- `incrementViews(Long snippetId)`: Increments view count
- `likeSnippet(Long snippetId)`: Likes a snippet
- `unlikeSnippet(Long snippetId)`: Unlikes a snippet
- `deleteSnippet(Long snippetId)`: Deletes a snippet
- `getAllLanguages()`: Gets all programming languages used

## Error Handling

### Common Errors
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Missing or invalid authentication token
- `403 Forbidden`: Attempting to access/modify resources without permission
- `404 Not Found`: Snippet or user not found
- `500 Internal Server Error`: Server-side errors

### Security Checks
- Ownership verification for update/delete operations
- Public/private access control for viewing
- Authentication required for create/update/delete/like operations

## Usage Examples

### Creating a Snippet
```bash
curl -X POST http://localhost:8080/api/snippets \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Hello World in Java",
    "description": "A simple Hello World program in Java",
    "code": "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}",
    "language": "java",
    "tags": ["hello-world", "beginner"],
    "isPublic": true
  }'
```

### Getting Public Snippets
```bash
curl http://localhost:8080/api/snippets/public?page=0&size=10&sort=createdAt,desc
```

### Searching Snippets
```bash
curl "http://localhost:8080/api/snippets/search?title=hello"
```

### Liking a Snippet
```bash
curl -X POST http://localhost:8080/api/snippets/1/like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Implementation Status
✅ **COMPLETED**: Snippet Management system is fully implemented and ready for use.

## Next Steps
This completes the Snippet Management system. The next features to implement are:
1. Blog Management (CRUD operations for blog posts)
2. Comment Management (CRUD operations for comments)
3. AI-powered features (code review, bug fixing)
4. GitHub OAuth integration
5. File upload services
6. Email notification system
