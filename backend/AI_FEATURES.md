# AI-Powered Features Documentation

## Overview

The AI-powered features provide intelligent code assistance using Groq AI integration. Users can leverage AI for code review, bug fixing, optimization, explanation, and code generation.

## Endpoints

### Authentication
All AI endpoints require JWT authentication with USER role.

### Base URL
```
/api/ai
```

### Available Endpoints

#### 1. Code Review
**POST** `/api/ai/review`

Reviews code and provides improvement suggestions.

**Request Body:**
```json
{
  "code": "function fibonacci(n) {\n  if (n <= 1) return n;\n  return fibonacci(n-1) + fibonacci(n-2);\n}",
  "language": "javascript",
  "context": "This is a recursive fibonacci implementation"
}
```

**Response:**
```json
{
  "id": 1,
  "type": "CODE_REVIEW",
  "status": "COMPLETED",
  "responseText": "Code review analysis...",
  "suggestedCode": null,
  "suggestions": ["Use memoization for optimization", "Add input validation"],
  "issues": ["Exponential time complexity", "No error handling"],
  "confidenceScore": 85,
  "processingTimeMs": 1500,
  "tokenUsage": {
    "promptTokens": 150,
    "completionTokens": 300,
    "totalTokens": 450
  },
  "createdAt": "2025-06-25T10:30:00"
}
```

#### 2. Bug Fix
**POST** `/api/ai/bug-fix`

Analyzes bugs and provides fix suggestions.

**Request Body:**
```json
{
  "code": "function divide(a, b) {\n  return a / b;\n}",
  "language": "javascript",
  "bugDescription": "Function throws error when b is zero",
  "expectedBehavior": "Should handle division by zero gracefully"
}
```

#### 3. Code Optimization
**POST** `/api/ai/optimize`

Optimizes code for better performance.

**Request Body:**
```json
{
  "code": "function sum(arr) {\n  let total = 0;\n  for(let i = 0; i < arr.length; i++) {\n    total += arr[i];\n  }\n  return total;\n}",
  "language": "javascript",
  "optimizationGoals": ["performance", "readability"]
}
```

#### 4. Code Explanation
**POST** `/api/ai/explain`

Explains how code works with detailed analysis.

**Request Body:**
```json
{
  "code": "const quickSort = (arr) => {\n  if (arr.length <= 1) return arr;\n  const pivot = arr[0];\n  const left = arr.slice(1).filter(x => x < pivot);\n  const right = arr.slice(1).filter(x => x >= pivot);\n  return [...quickSort(left), pivot, ...quickSort(right)];\n}",
  "language": "javascript",
  "focusAreas": ["algorithm", "time complexity"]
}
```

#### 5. Code Generation
**POST** `/api/ai/generate`

Generates code based on natural language descriptions.

**Request Body:**
```json
{
  "description": "Create a function that validates email addresses using regex",
  "language": "javascript",
  "requirements": ["Must support common email formats", "Return boolean result"],
  "style": "functional"
}
```

#### 6. Get AI History
**GET** `/api/ai/history?page=0&size=20`

Retrieves paginated history of AI requests for the current user.

**Query Parameters:**
- `page` (default: 0): Page number
- `size` (default: 20): Page size

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "CODE_REVIEW",
      "status": "COMPLETED",
      "responseText": "...",
      "createdAt": "2025-06-25T10:30:00"
    }
  ],
  "totalElements": 25,
  "totalPages": 2,
  "size": 20,
  "number": 0
}
```

#### 7. Get AI Request Details
**GET** `/api/ai/request/{requestId}`

Retrieves detailed information about a specific AI request.

**Response:**
```json
{
  "id": 1,
  "type": "CODE_REVIEW",
  "status": "COMPLETED",
  "responseText": "Detailed analysis...",
  "suggestedCode": "optimized code...",
  "suggestions": ["suggestion1", "suggestion2"],
  "issues": ["issue1"],
  "confidenceScore": 85,
  "processingTimeMs": 1500,
  "tokenUsage": {
    "promptTokens": 150,
    "completionTokens": 300,
    "totalTokens": 450
  },
  "createdAt": "2025-06-25T10:30:00"
}
```

## Request/Response Models

### Common Request Fields
- `code`: The source code to analyze (required for most endpoints)
- `language`: Programming language (e.g., "javascript", "python", "java")
- `context`: Additional context about the code (optional)

### Response Fields
- `id`: Unique request identifier
- `type`: Type of AI request (CODE_REVIEW, BUG_FIX, etc.)
- `status`: Request status (PENDING, PROCESSING, COMPLETED, FAILED)
- `responseText`: Full AI response text
- `suggestedCode`: AI-suggested code improvements (when applicable)
- `suggestions`: List of improvement suggestions
- `issues`: List of identified issues
- `confidenceScore`: AI confidence level (0-100)
- `processingTimeMs`: Time taken to process the request
- `tokenUsage`: Token consumption details
- `createdAt`: Request creation timestamp

## Supported Languages

The AI features support analysis of code in multiple programming languages:
- JavaScript/TypeScript
- Python
- Java
- C/C++
- Go
- Rust
- PHP
- Ruby
- Swift
- Kotlin
- And many more...

## Usage Examples

### Frontend Integration (JavaScript)

```javascript
// Code Review Example
const reviewCode = async (code, language) => {
  const response = await fetch('/api/ai/review', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      code,
      language,
      context: 'Function for user authentication'
    })
  });
  
  const result = await response.json();
  console.log('Review suggestions:', result.suggestions);
};

// Get AI History
const getAiHistory = async (page = 0, size = 20) => {
  const response = await fetch(`/api/ai/history?page=${page}&size=${size}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  const history = await response.json();
  return history;
};
```

### cURL Examples

```bash
# Code Review
curl -X POST "http://localhost:8080/api/ai/review" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "function add(a, b) { return a + b; }",
    "language": "javascript",
    "context": "Simple addition function"
  }'

# Bug Fix
curl -X POST "http://localhost:8080/api/ai/bug-fix" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "function divide(a, b) { return a / b; }",
    "language": "javascript",
    "bugDescription": "Crashes when b is zero",
    "expectedBehavior": "Should handle division by zero"
  }'

# Get AI History
curl -X GET "http://localhost:8080/api/ai/history?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Error Handling

All endpoints return appropriate HTTP status codes:

- `200 OK`: Successful request
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `500 Internal Server Error`: Server processing error

Error responses include descriptive messages:
```json
{
  "error": "Invalid code syntax",
  "message": "The provided code contains syntax errors that prevent analysis"
}
```

## Rate Limiting

AI endpoints may have rate limiting applied to prevent abuse:
- Maximum requests per user per hour
- Token usage limits
- Concurrent request restrictions

## Performance Considerations

- AI processing typically takes 1-5 seconds
- Larger code files take longer to process
- Complex analysis requests consume more tokens
- Consider caching frequent requests

## Best Practices

1. **Provide Context**: Include relevant context for better AI analysis
2. **Reasonable Code Size**: Limit code to reasonable sizes (< 10KB)
3. **Clear Descriptions**: Use clear, specific descriptions for code generation
4. **Review Results**: Always review AI suggestions before implementation
5. **Error Handling**: Implement proper error handling for API calls
6. **Token Management**: Monitor token usage to avoid limits

## Security

- All requests require authentication
- User data is not stored permanently
- AI requests are logged for debugging purposes
- Rate limiting prevents abuse
- Input validation prevents injection attacks
