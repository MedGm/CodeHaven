# Blog and Comment Management System

## Overview
The Blog and Comment Management system provides a complete blogging platform with CRUD operations for blog posts, hierarchical commenting system, social features, and content management capabilities.

## Architecture
The implementation follows Clean Architecture principles with clear separation of concerns:

- **Domain Layer**: `Blog`, `Comment` entities and service interfaces
- **Infrastructure Layer**: JPA repositories and service implementations
- **Application Layer**: DTOs, mappers, and use cases
- **Controller Layer**: REST API endpoints

## Blog Management

### Blog Statuses
- **DRAFT**: Unpublished blog, visible only to author
- **PUBLISHED**: Public blog, visible to everyone
- **ARCHIVED**: Archived blog, treated like unpublished

### API Endpoints

#### Core Blog Operations

#### Create Blog
```http
POST /api/blogs
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "String (required, max 255 chars)",
  "content": "String (required)",
  "excerpt": "String (optional, max 500 chars, auto-generated if empty)",
  "coverImageUrl": "String (optional)",
  "tags": ["String array (optional)"],
  "readingTime": "Integer (optional, auto-calculated if not provided)",
  "isFeatured": "Boolean (optional, default false)",
  "status": "String (optional: DRAFT/PUBLISHED/ARCHIVED, default DRAFT)"
}
```

#### Update Blog
```http
PUT /api/blogs/{blogId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "String (required, max 255 chars)",
  "content": "String (required)",
  "excerpt": "String (optional, max 500 chars)",
  "coverImageUrl": "String (optional)",
  "tags": ["String array (optional)"],
  "readingTime": "Integer (optional)",
  "isFeatured": "Boolean (optional)",
  "status": "String (optional: DRAFT/PUBLISHED/ARCHIVED)"
}
```

#### Get Blog by ID
```http
GET /api/blogs/{blogId}
```

#### Delete Blog
```http
DELETE /api/blogs/{blogId}
Authorization: Bearer {token}
```

### Blog Discovery

#### Get Published Blogs
```http
GET /api/blogs/published?page=0&size=20&sort=publishedAt,desc
```

#### Get User's Blogs
```http
GET /api/blogs/users/{username}?page=0&size=20&sort=createdAt,desc
```

#### Search Blogs by Title
```http
GET /api/blogs/search?title={searchTerm}
```

#### Get Blogs by Tag
```http
GET /api/blogs/tag/{tag}
```

#### Get Featured Blogs
```http
GET /api/blogs/featured?page=0&size=20&sort=publishedAt,desc
```

#### Get Trending Blogs
```http
GET /api/blogs/trending
```

### Blog Actions

#### Publish Blog
```http
POST /api/blogs/{blogId}/publish
Authorization: Bearer {token}
```

#### Unpublish Blog
```http
POST /api/blogs/{blogId}/unpublish
Authorization: Bearer {token}
```

#### Like Blog
```http
POST /api/blogs/{blogId}/like
Authorization: Bearer {token}
```

#### Unlike Blog
```http
DELETE /api/blogs/{blogId}/like
Authorization: Bearer {token}
```

#### Toggle Featured Status
```http
POST /api/blogs/{blogId}/toggle-featured
Authorization: Bearer {token}
```

## Comment Management

### API Endpoints

#### Core Comment Operations

#### Create Comment
```http
POST /api/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "String (required)",
  "blogId": "Long (required)",
  "parentCommentId": "Long (optional, for replies)"
}
```

#### Update Comment
```http
PUT /api/comments/{commentId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "String (required)"
}
```

#### Get Comment by ID
```http
GET /api/comments/{commentId}
```

#### Delete Comment
```http
DELETE /api/comments/{commentId}
Authorization: Bearer {token}
```

### Comment Discovery

#### Get Comments by Blog
```http
GET /api/comments/blog/{blogId}?page=0&size=20&sort=createdAt,asc
```

#### Get User's Comments
```http
GET /api/comments/user/{username}
```

#### Get Replies to Comment
```http
GET /api/comments/{commentId}/replies
```

### Comment Actions

#### Like Comment
```http
POST /api/comments/{commentId}/like
Authorization: Bearer {token}
```

#### Unlike Comment
```http
DELETE /api/comments/{commentId}/like
Authorization: Bearer {token}
```

## Data Models

### CreateBlogRequest
```json
{
  "title": "String (required, max 255 chars)",
  "content": "String (required)",
  "excerpt": "String (optional, max 500 chars)",
  "coverImageUrl": "String (optional)",
  "tags": ["String array (optional)"],
  "readingTime": "Integer (optional)",
  "isFeatured": "Boolean (optional)",
  "status": "String (DRAFT/PUBLISHED/ARCHIVED)"
}
```

### UpdateBlogRequest
```json
{
  "title": "String (required, max 255 chars)",
  "content": "String (required)",
  "excerpt": "String (optional, max 500 chars)",
  "coverImageUrl": "String (optional)",
  "tags": ["String array (optional)"],
  "readingTime": "Integer (optional)",
  "isFeatured": "Boolean (optional)",
  "status": "String (DRAFT/PUBLISHED/ARCHIVED)"
}
```

### BlogDto
```json
{
  "id": "Long",
  "title": "String",
  "content": "String",
  "excerpt": "String",
  "coverImageUrl": "String",
  "tags": ["String array"],
  "likesCount": "Integer",
  "viewsCount": "Integer",
  "readingTime": "Integer",
  "status": "String",
  "isFeatured": "Boolean",
  "isLikedByCurrentUser": "Boolean",
  "authorUsername": "String",
  "authorName": "String",
  "commentsCount": "Integer",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime",
  "publishedAt": "LocalDateTime"
}
```

### CreateCommentRequest
```json
{
  "content": "String (required)",
  "blogId": "Long (required)",
  "parentCommentId": "Long (optional)"
}
```

### UpdateCommentRequest
```json
{
  "content": "String (required)"
}
```

### CommentDto
```json
{
  "id": "Long",
  "content": "String",
  "likesCount": "Integer",
  "isEdited": "Boolean",
  "isLikedByCurrentUser": "Boolean",
  "authorUsername": "String",
  "authorName": "String",
  "blogId": "Long",
  "parentCommentId": "Long",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## Security & Authorization

### Blog Security
- **Create/Update/Delete**: Only blog owner
- **Publish/Unpublish**: Only blog owner
- **Toggle Featured**: Only blog owner (can be extended for admin)
- **View**: Published blogs are public, drafts only visible to owner
- **Like**: Authenticated users, only on published blogs

### Comment Security
- **Create**: Authenticated users, only on published blogs
- **Update/Delete**: Only comment owner
- **View**: Public for comments on published blogs
- **Reply**: Must belong to the same blog as parent comment
- **Like**: Authenticated users, only on published blog comments

## Features

### Blog Features
- ✅ Create, read, update, delete blogs
- ✅ Draft/Published/Archived status management
- ✅ Auto-generated excerpts and reading time calculation
- ✅ Tag-based categorization
- ✅ Featured blogs system
- ✅ Cover image support
- ✅ View count tracking
- ✅ Like/unlike functionality
- ✅ Search by title
- ✅ Trending and popular blogs
- ✅ User-specific blog listings
- ✅ Pagination support

### Comment Features
- ✅ Create, read, update, delete comments
- ✅ Hierarchical comment system (replies)
- ✅ Edit tracking (isEdited flag)
- ✅ Like/unlike functionality
- ✅ User-specific comment listings
- ✅ Blog-specific comment listings
- ✅ Reply system with parent-child relationships

### Smart Features
- ✅ Auto-excerpt generation from content
- ✅ Auto-reading time calculation
- ✅ Published date tracking
- ✅ Edit history tracking for comments
- ✅ Nested comment replies

## Database Schema

### blogs table
- `id` (Primary Key)
- `title` (VARCHAR, NOT NULL)
- `content` (TEXT, NOT NULL)
- `excerpt` (TEXT)
- `cover_image_url` (VARCHAR)
- `views` (BIGINT, DEFAULT 0)
- `likes` (BIGINT, DEFAULT 0)
- `reading_time` (INTEGER)
- `status` (VARCHAR, NOT NULL, DEFAULT 'DRAFT')
- `is_featured` (BOOLEAN, DEFAULT false)
- `user_id` (Foreign Key to users table)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `published_at` (TIMESTAMP)

### blog_tags table
- `blog_id` (Foreign Key)
- `tag` (VARCHAR)

### comments table
- `id` (Primary Key)
- `content` (TEXT, NOT NULL)
- `likes` (BIGINT, DEFAULT 0)
- `is_edited` (BOOLEAN, DEFAULT false)
- `blog_id` (Foreign Key to blogs table)
- `user_id` (Foreign Key to users table)
- `parent_comment_id` (Foreign Key to comments table, nullable)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Service Layer Methods

### BlogService Interface
- `createBlog(Blog blog)`: Creates a new blog
- `updateBlog(Long blogId, Blog blogUpdate)`: Updates an existing blog
- `findById(Long blogId)`: Finds blog by ID
- `findPublishedBlogs(Pageable pageable)`: Gets paginated published blogs
- `findBlogsByUser(User user, Pageable pageable)`: Gets user's blogs
- `findBlogsByUserAndStatus(User user, Status status, Pageable pageable)`: Gets user's blogs by status
- `searchBlogsByTitle(String title)`: Searches blogs by title
- `findBlogsByTag(String tag)`: Finds blogs by tag
- `findFeaturedBlogs(Pageable pageable)`: Gets featured blogs
- `getTrendingBlogs()`: Gets trending blogs
- `publishBlog(Long blogId)`: Publishes a blog
- `unpublishBlog(Long blogId)`: Unpublishes a blog
- `incrementViews(Long blogId)`: Increments view count
- `likeBlog(Long blogId)`: Likes a blog
- `unlikeBlog(Long blogId)`: Unlikes a blog
- `deleteBlog(Long blogId)`: Deletes a blog
- `toggleFeatured(Long blogId)`: Toggles featured status
- `calculateReadingTime(String content)`: Calculates reading time
- `generateExcerpt(String content, int maxLength)`: Generates excerpt

### CommentService Interface
- `createComment(Comment comment)`: Creates a new comment
- `updateComment(Long commentId, Comment commentUpdate)`: Updates a comment
- `findById(Long commentId)`: Finds comment by ID
- `findCommentsByBlog(Blog blog, Pageable pageable)`: Gets comments for a blog
- `findCommentsByUser(User user)`: Gets user's comments
- `findRepliesByParentComment(Comment parentComment)`: Gets replies to a comment
- `likeComment(Long commentId)`: Likes a comment
- `unlikeComment(Long commentId)`: Unlikes a comment
- `deleteComment(Long commentId)`: Deletes a comment

## Error Handling

### Common Errors
- `400 Bad Request`: Invalid input data, invalid status transitions
- `401 Unauthorized`: Missing or invalid authentication token
- `403 Forbidden`: Attempting to access/modify resources without permission
- `404 Not Found`: Blog or comment not found
- `500 Internal Server Error`: Server-side errors

### Business Rules
- Only published blogs can be viewed by non-owners
- Only published blogs can receive comments
- Comments can only be made on published blogs
- Users can only edit/delete their own content
- Replies must belong to the same blog as parent comment

## Usage Examples

### Creating a Blog
```bash
curl -X POST http://localhost:8080/api/blogs \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Getting Started with Spring Boot",
    "content": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications...",
    "tags": ["spring-boot", "java", "tutorial"],
    "status": "PUBLISHED"
  }'
```

### Creating a Comment
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Great article! Very helpful for beginners.",
    "blogId": 1
  }'
```

### Creating a Reply
```bash
curl -X POST http://localhost:8080/api/comments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "I agree! The examples are very clear.",
    "blogId": 1,
    "parentCommentId": 1
  }'
```

### Getting Published Blogs
```bash
curl "http://localhost:8080/api/blogs/published?page=0&size=10&sort=publishedAt,desc"
```

### Liking a Blog
```bash
curl -X POST http://localhost:8080/api/blogs/1/like \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Implementation Status
✅ **COMPLETED**: Blog and Comment Management systems are fully implemented and ready for use.

## Key Features Summary

### Blog Management
- Full CRUD operations with owner-only access control
- Draft/Published/Archived workflow
- Auto-excerpt and reading time calculation
- Tag-based organization and search
- Featured blogs system
- Social features (likes, views)
- Trending and discovery features

### Comment Management  
- Hierarchical comment system with replies
- Edit tracking and moderation
- Owner-only edit/delete permissions
- Social features (likes)
- Blog-specific and user-specific listings

The Blog and Comment Management system provides a complete, production-ready blogging platform with advanced features for content creation, discovery, and community interaction.
