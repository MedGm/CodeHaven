# CodeHaven 🚀

**CodeHaven** is a comprehensive web application designed for developers to manage their coding projects, share code snippets, write technical blogs, and leverage AI-powered development tools. Built with modern technologies and featuring a robust notification system and dynamic user profiles.

## 🌟 Features

### 📁 Project Management
- Create, organize, and manage coding projects
- Star favorite projects for quick access
- Filter projects by recent activity or starred status
- Project image uploads and file management

### 📝 Technical Blogging
- Create and publish technical blog posts
- Draft management system
- Rich text editing capabilities
- Blog categorization and tagging

### 💻 Code Snippets
- Share code snippets with the community
- Public and private snippet management
- Syntax highlighting support
- Language-specific categorization

### 🤖 AI-Powered Tools
- **Code Review**: AI-powered code analysis and suggestions
- **Bug Fix**: Intelligent bug detection and fixing recommendations
- **Code Generation**: Generate code from natural language descriptions
- **Code Optimization**: Performance and efficiency improvements
- **Code Explanation**: Detailed explanations of complex code

### 👤 User Management
- Secure user authentication with JWT
- Dynamic user profiles with avatar support
- Real-time profile updates across the application
- GitHub username integration

### 🔔 Notification System
- Real-time notifications for user activities
- Welcome notifications for new users
- Sample notifications for enhanced user experience
- Notification management (mark as read, delete)

### 🎨 Modern UI/UX
- Dark/Light theme toggle
- Responsive design for all devices
- GitHub-inspired interface
- Smooth animations and transitions
- Mobile-friendly sidebar navigation

## 🏗️ Architecture

### Frontend (Angular 19)
- **Framework**: Angular 19.2.0 with TypeScript
- **Styling**: Tailwind CSS with custom design system
- **UI Components**: Angular Material 19
- **State Management**: RxJS with BehaviorSubjects
- **Code Highlighting**: Highlight.js
- **Icons**: Lucide Angular

### Backend (Spring Boot 3)
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 17
- **Database**: JPA/Hibernate with H2 (development)
- **Security**: Spring Security with JWT authentication
- **Caching**: Redis for performance optimization
- **Validation**: Bean Validation (JSR-380)
- **Testing**: JUnit 5 with Spring Boot Test

## 📂 Project Structure

```
CodeHaven/
├── frontend/                    # Angular frontend application
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/           # Core services and models
│   │   │   │   ├── services/   # Authentication, User, Notification services
│   │   │   │   ├── models/     # TypeScript interfaces and models
│   │   │   │   └── interceptors/ # HTTP interceptors
│   │   │   ├── features/       # Feature modules
│   │   │   │   ├── auth/       # Authentication components
│   │   │   │   ├── profile/    # User profile management
│   │   │   │   ├── projects/   # Project management
│   │   │   │   ├── blogs/      # Blog management
│   │   │   │   ├── snippets/   # Code snippet management
│   │   │   │   └── ai/         # AI-powered tools
│   │   │   ├── layout/         # Layout components
│   │   │   │   ├── navbar/     # Top navigation
│   │   │   │   └── sidebar/    # Side navigation
│   │   │   └── shared/         # Shared components and utilities
│   │   └── environments/       # Environment configurations
│   ├── package.json
│   └── tailwind.config.js
│
└── backend/                     # Spring Boot backend application
    ├── src/
    │   ├── main/
    │   │   ├── java/com/codehaven/backend/
    │   │   │   ├── application/    # Application layer
    │   │   │   │   └── dto/        # Data Transfer Objects
    │   │   │   ├── controller/     # REST API controllers
    │   │   │   ├── domain/         # Domain layer
    │   │   │   │   ├── model/      # Entity models
    │   │   │   │   ├── repository/ # Repository interfaces
    │   │   │   │   └── service/    # Business logic interfaces
    │   │   │   ├── infrastructure/ # Infrastructure layer
    │   │   │   │   └── service/    # Service implementations
    │   │   │   ├── security/       # Security configuration
    │   │   │   └── config/         # Application configuration
    │   │   └── resources/
    │   │       ├── application.properties
    │   │       └── static/         # Static file serving
    │   └── test/                   # Unit and integration tests
    └── pom.xml
```

## 🚀 Getting Started

### Prerequisites
- **Node.js** 18+ and npm
- **Java** 17+
- **Maven** 3.6+
- **Git**

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

   The application will be available at `http://localhost:4200`

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

### Full Application Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd CodeHaven
   ```

2. Start the backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

3. In a new terminal, start the frontend:
   ```bash
   cd frontend
   npm install
   npm start
   ```

4. Open your browser and navigate to `http://localhost:4200`

## 🔧 Configuration

### Environment Variables

#### Frontend (`frontend/src/environments/environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  groqApiKey: 'your-groq-api-key'
};
```

#### Backend (`backend/src/main/resources/application.properties`)
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# JWT Configuration
app.jwtSecret=your-jwt-secret
app.jwtExpirationInMs=604800000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Redis Configuration (Optional)
spring.redis.host=localhost
spring.redis.port=6379
```

## 🎯 Key Features Implementation

### Dynamic Avatar System
- **Frontend**: Real-time avatar updates across all components after profile changes
- **Backend**: Secure file upload endpoints with proper access control
- **Integration**: Seamless avatar URL construction and serving

### Robust Notification System
- **Backend**: Automatic sample notification creation for new users
- **Frontend**: Fallback notifications ensure UI never appears empty
- **Real-time Updates**: Notifications update dynamically after login/logout

### JWT Authentication with Token Refresh
- **Secure Token Management**: Automatic token validation and refresh
- **Profile Synchronization**: User profile data stays synchronized across the app
- **Error Handling**: Graceful handling of expired tokens and authentication errors

### File Upload and Management
- **Image Processing**: Support for project images and user avatars
- **Security**: Path variable-based file serving with proper access control
- **Storage**: Organized file structure for different content types

## 🧪 Testing

### Frontend Testing
```bash
cd frontend
npm run test
```

### Backend Testing
```bash
cd backend
./mvnw test
```

## 🚀 Deployment

### Frontend Build
```bash
cd frontend
npm run build
```

### Backend Build
```bash
cd backend
./mvnw package
```

## 🛠️ Technology Stack

### Frontend Technologies
- **Angular 19**: Latest Angular framework with standalone components
- **TypeScript**: Type-safe JavaScript development
- **Tailwind CSS**: Utility-first CSS framework
- **Angular Material**: Material Design components
- **RxJS**: Reactive programming with Observables
- **Highlight.js**: Syntax highlighting for code snippets

### Backend Technologies
- **Spring Boot 3**: Modern Java framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence layer
- **JWT (JSON Web Tokens)**: Stateless authentication
- **H2 Database**: In-memory database for development
- **Redis**: Caching and performance optimization
- **Maven**: Dependency management and build tool

### Development Tools
- **Angular CLI**: Angular development toolkit
- **Spring Boot DevTools**: Hot reloading for backend
- **VS Code**: Recommended IDE with extensions
- **Postman**: API testing and documentation

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Development Team** - *Initial work and ongoing development*

## 🙏 Acknowledgments

- Angular team for the excellent framework
- Spring Boot team for the robust backend framework
- Tailwind CSS for the amazing utility-first CSS framework
- All open-source contributors who made this project possible

---

**CodeHaven** - *Empowering developers with modern tools and AI assistance* 🚀