# 🚀 Backend - Team 1 Full Stack Project Lab

> RESTful and GraphQL API server for a travel booking platform, built with Spring Boot and Kotlin.

This is the backend portion of a comprehensive travel booking platform, providing robust APIs for accommodation searches, bookings, user management, and host property administration.

## 📋 Table of Contents

-   [Overview](#overview)
-   [Features](#features)
-   [Tech Stack](#tech-stack)
-   [Architecture](#architecture)
-   [Project Structure](#project-structure)
-   [Getting Started](#getting-started)
-   [Database Setup](#database-setup)
-   [Environment Variables](#environment-variables)
-   [API Documentation](#api-documentation)
-   [Key Concepts](#key-concepts)
-   [Testing](#testing)
-   [Security](#security)
-   [Development Guidelines](#development-guidelines)
-   [Learning Outcomes](#learning-outcomes)

## 🎯 Overview

This Spring Boot application serves as the backend for a full-featured travel accommodation booking platform. It provides:

-   **Dual API Support**: Both REST and GraphQL endpoints
-   **Authentication & Authorization**: JWT-based security with role management
-   **Database Management**: PostgreSQL with Flyway migrations
-   **Business Logic**: Comprehensive services for stays, bookings, users, and companies
-   **AI Integration**: Koog AI agent for customer support chatbot

### System Capabilities

1. **User Management**: Registration, authentication, profile management
2. **Accommodation Search**: Advanced filtering, geolocation search, pagination
3. **Booking System**: Trip creation, stay unit reservations
4. **Host Management**: Company registration, property CRUD operations
5. **AI Assistant**: Context-aware chatbot for customer support

## ✨ Features

### API Features

-   🔐 JWT-based authentication and authorization
-   📡 Dual API: REST + GraphQL support
-   🔍 Advanced search with multiple filters
-   📍 Geolocation-based stay search
-   📄 Pagination and sorting
-   ✅ Input validation with detailed error messages
-   🛡️ Spring Security integration
-   📚 Interactive API documentation (Swagger/GraphiQL)

### Business Features

-   👤 User registration and authentication
-   🏢 Company registration for hosts
-   🏨 Stay (property) management with units (rooms)
-   📸 Image management for properties
-   🎯 Service/amenity associations
-   🌍 Geographic data (countries, states, cities)
-   📊 Trip management and booking history
-   🤖 AI-powered chatbot assistance

### Data Features

-   💾 PostgreSQL database with JPA/Hibernate
-   🔄 Database migrations with Flyway
-   🎨 Data seeding for development
-   🔗 Complex entity relationships
-   🗃️ Optimized queries with specifications

## 🛠️ Tech Stack

### Core Framework

-   **Spring Boot** - Application framework
-   **Kotlin** - Primary programming language
-   **Java** - JVM runtime

### Spring Ecosystem

-   **Spring Web** - REST API endpoints
-   **Spring Data JPA** - Database persistence
-   **Spring Security** - Authentication & authorization
-   **Spring Validation** - Input validation

### Database

-   **PostgreSQL** - Primary database
-   **Flyway** - Database migrations
-   **Hibernate** - ORM framework

### Additional Libraries

-   **JWT (jjwt)** - Token-based authentication
-   **Koog** - AI agent integration
-   **Jackson** - JSON serialization
-   **SpringDoc OpenAPI** - API documentation (Swagger)
-   **Kotlin Coroutines** - Asynchronous programming
-   **Kotlin Serialization** - Data serialization

### Testing

-   **Kotest** - Kotlin testing framework
-   **MockK** - Mocking library
-   **Spring Test** - Integration testing
-   **JUnit 5** - Test runner

### Build & Quality

-   **Gradle Kotlin DSL** - Build automation
-   **ktlint** - Kotlin code formatting

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controllers/Resolvers        │  ← REST/GraphQL endpoints
├─────────────────────────────────────┤
│            DTOs/Mappers              │  ← Data Transfer Objects
├─────────────────────────────────────┤
│            Services                  │  ← Business logic
├─────────────────────────────────────┤
│        Repositories/JPA              │  ← Data access
├─────────────────────────────────────┤
│           Database (PostgreSQL)      │  ← Persistence
└─────────────────────────────────────┘
```

### API Patterns

**REST API**

-   Resource-based URLs (`/api/stays`, `/api/trips`)
-   HTTP methods (GET, POST, PUT, DELETE)
-   JSON request/response bodies
-   Standard HTTP status codes

**GraphQL API**

-   Single endpoint (`/graphql`)
-   Query and Mutation operations
-   Flexible data fetching
-   Type-safe schema

## 📁 Project Structure

```
Backend/
├── src/
│   ├── main/
│   │   ├── kotlin/edu/fullstackproject/team1/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── CorsConfig.kt
│   │   │   │   ├── GraphQLConfig.kt
│   │   │   │   └── OpenApiConfig.kt
│   │   │   ├── controllers/         # REST endpoints
│   │   │   │   ├── AuthController.kt
│   │   │   │   ├── StayController.kt
│   │   │   │   ├── TripController.kt
│   │   │   │   └── ...
│   │   │   ├── resolver/            # GraphQL resolvers
│   │   │   │   ├── Query.kt
│   │   │   │   └── Mutation.kt
│   │   │   ├── dtos/                # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── models/              # JPA entities
│   │   │   │   ├── User.kt
│   │   │   │   ├── Stay.kt
│   │   │   │   ├── StayUnit.kt
│   │   │   │   └── ...
│   │   │   ├── repositories/        # Data access layer
│   │   │   │   ├── UserRepository.kt
│   │   │   │   ├── StayRepository.kt
│   │   │   │   └── ...
│   │   │   ├── services/            # Business logic
│   │   │   │   ├── AuthService.kt
│   │   │   │   ├── StayService.kt
│   │   │   │   ├── TripService.kt
│   │   │   │   └── ...
│   │   │   ├── security/            # Security configuration
│   │   │   │   ├── JwtAuthenticationFilter.kt
│   │   │   │   ├── SecurityConfig.kt
│   │   │   │   └── UserDetailsServiceImpl.kt
│   │   │   ├── specifications/      # JPA specifications for queries
│   │   │   ├── mappers/             # Entity ↔ DTO mappers
│   │   │   ├── exceptions/          # Custom exceptions
│   │   │   ├── tool/                # AI agent tools
│   │   │   ├── utils/               # Utility classes
│   │   │   └── Application.kt       # Main entry point
│   │   └── resources/
│   │       ├── application.yml      # Application configuration
│   │       └── db/migration/        # Flyway migrations
│   │           ├── V1__Initial_schema.sql
│   │           ├── V2__Add_stays.sql
│   │           └── ...
│   └── test/
│       └── kotlin/                  # Test files
├── build.gradle.kts                 # Gradle build configuration
├── settings.gradle.kts
└── Dockerfile                       # Docker configuration
```

## 🚀 Getting Started

### Prerequisites

-   **Java JDK 21** or higher
-   **PostgreSQL 15+** database server
-   **Gradle 8+** (or use included wrapper)
-   **Docker** (optional, for containerization)

### Installation

1. Clone the repository:

```bash
git clone https://github.com/Team-1-Full-Stack-Project-Lab/Backend.git
cd Backend
```

2. Set up PostgreSQL database:

```bash
# Create database
createdb travel_booking_db

# Or using psql
psql -U postgres
CREATE DATABASE travel_booking_db;
```

3. Configure environment variables (see [Environment Variables](#environment-variables))

4. Run database migrations:

```bash
./gradlew flywayMigrate
```

5. Build the project:

```bash
./gradlew build
```

6. Run the application:

```bash
./gradlew bootRun
```

The server will start at `http://localhost:8080`

### Quick Start with Docker

```bash
# Build image
docker build -t travel-booking-backend .

# Run container (uses default values from application.properties)
docker run -p 8080:8080 travel-booking-backend

# Or with custom environment variables
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/fullstack_project" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="production-secret-key" \
  travel-booking-backend
```

## 💾 Database Setup

### Using Flyway Migrations

Migrations are located in `src/main/resources/db/migration/`

```bash
# Apply migrations
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo

# Rollback (clean database)
./gradlew flywayClean
```

### Database Schema Overview

**Core Tables:**

-   `users` - User accounts
-   `companies` - Host companies
-   `stays` - Accommodation properties
-   `stay_units` - Individual rooms/units
-   `trips` - User bookings
-   `trip_stay_units` - Booked units
-   `cities`, `states`, `countries` - Geographic data
-   `services` - Amenities
-   `stay_types` - Property types

**Relationships:**

-   User 1:1 Company (optional)
-   Company 1:N Stays
-   Stay 1:N StayUnits
-   Stay N:M Services
-   User 1:N Trips
-   Trip N:M StayUnits

## 🔧 Environment Variables

### Configuration Approach

This project uses `application.properties` with **default values** for development. You can override these values using **system environment variables** for different environments (production, staging, etc.).

### application.properties

Located at `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=Team 1 Full Stack Project Lab
server.port=${APP_PORT:8080}

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/fullstack_project}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.show-sql=${APP_DEBUG:true}
spring.jpa.properties.hibernate.format_sql=${APP_DEBUG:true}

# JWT Configuration
jwt.secret=${JWT_SECRET:dev-secret-key-12345678901234567890123456789012}
jwt.expiration=${JWT_EXPIRATION:3600000}

# CORS
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173}

# API Documentation
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Google API
google.api.key=${GOOGLE_API_KEY:}
```

### How It Works

The pattern `${VARIABLE_NAME:default_value}` means:

-   If environment variable `VARIABLE_NAME` exists → use its value
-   Otherwise → use the default value

**Example**: `${DB_URL:jdbc:postgresql://localhost:5432/fullstack_project}`

-   Looks for `DB_URL` environment variable
-   If not found, uses `jdbc:postgresql://localhost:5432/fullstack_project`

### Setting Environment Variables

**Development (Local)**

-   Default values are already configured
-   Just run `./gradlew bootRun` - no setup needed!

**Production/Custom Environment**

**Linux/Mac:**

```bash
export DB_URL=jdbc:postgresql://production-host:5432/production_db
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export JWT_SECRET=your-production-secret-key-minimum-32-characters
export APP_DEBUG=false
export CORS_ALLOWED_ORIGINS=https://yourdomain.com

./gradlew bootRun
```

**Windows (PowerShell):**

```powershell
$env:DB_URL="jdbc:postgresql://production-host:5432/production_db"
$env:DB_USERNAME="prod_user"
$env:DB_PASSWORD="secure_password"
$env:JWT_SECRET="your-production-secret-key-minimum-32-characters"
$env:APP_DEBUG="false"
$env:CORS_ALLOWED_ORIGINS="https://yourdomain.com"

./gradlew bootRun
```

**Docker:**

```bash
docker run -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/fullstack_project" \
  -e DB_USERNAME="postgres" \
  -e DB_PASSWORD="password" \
  -e JWT_SECRET="production-secret-key" \
  travel-booking-backend
```

**IDE (IntelliJ IDEA):**

1. Run → Edit Configurations
2. Environment Variables → Add variables
3. Example: `DB_URL=jdbc:postgresql://localhost:5432/my_db;JWT_SECRET=my-secret`

### Required Variables for Production

⚠️ **Must override in production:**

-   `JWT_SECRET` - Use a strong, random 32+ character secret
-   `DB_PASSWORD` - Set a secure database password
-   `DB_URL` - Point to production database
-   `APP_DEBUG` - Set to `false`
-   `CORS_ALLOWED_ORIGINS` - Set to your frontend domain

### Optional Variables

Variables with good defaults (override if needed):

-   `APP_PORT` - Server port (default: 8080)
-   `DB_USERNAME` - Database user (default: postgres)
-   `JWT_EXPIRATION` - Token expiration in ms (default: 3600000 = 1 hour)
-   `GOOGLE_API_KEY` - For location services

### REST API

**Swagger UI**: http://localhost:8080/swagger-ui.html

**OpenAPI Spec**: http://localhost:8080/v3/api-docs

### GraphQL API

**GraphiQL Playground**: http://localhost:8080/graphiql

**Endpoint**: http://localhost:8080/graphql

### Main Endpoints

**Authentication**

```
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

**Stays (Properties)**

```
GET    /api/stays
GET    /api/stays/{id}
POST   /api/stays
PUT    /api/stays/{id}
DELETE /api/stays/{id}
GET    /api/stays/nearby
```

**Trips (Bookings)**

```
GET    /api/trips
GET    /api/trips/{id}
POST   /api/trips
PUT    /api/trips/{id}
DELETE /api/trips/{id}
```

**Companies**

```
GET    /api/companies/{id}
POST   /api/companies
PUT    /api/companies/{id}
DELETE /api/companies/{id}
```

**GraphQL Queries**

```graphql
query {
	getAllStays(cityId: 1, page: 0, size: 10) {
		content {
			id
			name
			city {
				name
			}
			units {
				pricePerNight
			}
		}
	}
}
```

## 💡 Key Concepts

### 1. Entity Relationships

```kotlin
@Entity
@Table(name = "stays")
class Stay(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "company_id")
    val company: Company,

    @OneToMany(mappedBy = "stay", cascade = [CascadeType.ALL])
    val units: List<StayUnit> = listOf(),

    @ManyToMany
    @JoinTable(
        name = "stay_services",
        joinColumns = [JoinColumn(name = "stay_id")],
        inverseJoinColumns = [JoinColumn(name = "service_id")]
    )
    val services: Set<Service> = setOf()
)
```

### 2. DTO Pattern

```kotlin
// Request DTO
data class StayCreateRequest(
    @field:NotBlank val name: String,
    @field:NotNull val cityId: Long,
    val serviceIds: List<Long> = listOf()
)

// Response DTO
data class StayResponse(
    val id: Long,
    val name: String,
    val city: CityResponse?,
    val services: List<ServiceResponse>
)

// Mapper
fun Stay.toResponse() = StayResponse(
    id = id,
    name = name,
    city = city?.toResponse(),
    services = services.map { it.toResponse() }
)
```

### 3. Service Layer

```kotlin
@Service
class StayService(
    private val stayRepository: StayRepository,
    private val companyRepository: CompanyRepository
) {
    fun createStay(request: StayCreateRequest, userId: Long): Stay {
        // Business logic
        val company = companyRepository.findByUserId(userId)
            ?: throw NotFoundException("Company not found")

        val stay = Stay(
            name = request.name,
            company = company,
            // ... other fields
        )

        return stayRepository.save(stay)
    }
}
```

### 4. Security Configuration

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
```

### 5. GraphQL Resolvers

```kotlin
@Component
class Query {
    fun getAllStays(
        @Argument cityId: Long?,
        @Argument page: Int = 0,
        @Argument size: Int = 10
    ): Page<Stay> {
        return stayService.getAllStays(cityId, PageRequest.of(page, size))
    }
}

@Component
class Mutation {
    fun createStay(@Argument request: StayCreateRequest): Stay {
        return stayService.createStay(request)
    }
}
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "StayServiceTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Test Structure

```kotlin
class StayServiceTest : FunSpec({
    val stayRepository = mockk<StayRepository>()
    val service = StayService(stayRepository)

    test("should create stay successfully") {
        // Given
        val request = StayCreateRequest(name = "Test Hotel")
        every { stayRepository.save(any()) } returns mockStay

        // When
        val result = service.createStay(request)

        // Then
        result.name shouldBe "Test Hotel"
        verify { stayRepository.save(any()) }
    }
})
```

## 🔒 Security

### Authentication Flow

1. User sends credentials to `/api/auth/login`
2. Server validates and returns JWT token
3. Client includes token in `Authorization: Bearer <token>` header
4. JWT filter validates token on each request
5. User details loaded for authorization

### Password Security

-   Passwords hashed using BCrypt
-   Never stored in plain text
-   Minimum validation requirements

### CORS Configuration

```kotlin
@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        // ...
    }
}
```

## 👨‍💻 Development Guidelines

### Code Style

-   Follow Kotlin coding conventions
-   Use `ktlint` for formatting
-   Prefer data classes for DTOs
-   Use meaningful names
-   Write KDoc for public APIs

### Commit Messages

```
feat: add stay search by location
fix: correct pagination offset calculation
refactor: extract validation logic to utility
docs: update API documentation
test: add tests for trip service
```

### Error Handling

```kotlin
// Custom exceptions
class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(val errors: Map<String, List<String>>) : RuntimeException()

// Global exception handler
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = ex.message))
    }
}
```

## 📚 Learning Outcomes

This project demonstrates understanding of:

### Backend Development

-   ✅ RESTful API design principles
-   ✅ GraphQL schema and resolvers
-   ✅ Spring Boot application structure
-   ✅ Dependency injection with Spring
-   ✅ Layered architecture pattern

### Database & Persistence

-   ✅ JPA/Hibernate ORM
-   ✅ Database design and relationships
-   ✅ Query optimization with Specifications
-   ✅ Database migrations with Flyway
-   ✅ Transaction management

### Security

-   ✅ JWT token authentication
-   ✅ Spring Security configuration
-   ✅ Password hashing and validation
-   ✅ CORS handling
-   ✅ Role-based authorization

### Kotlin Programming

-   ✅ Kotlin syntax and idioms
-   ✅ Data classes and sealed classes
-   ✅ Extension functions
-   ✅ Null safety
-   ✅ Coroutines (basics)

### Software Engineering

-   ✅ Clean code principles
-   ✅ SOLID principles
-   ✅ Unit and integration testing
-   ✅ API documentation
-   ✅ Error handling strategies
-   ✅ Logging and monitoring

### DevOps Basics

-   ✅ Gradle build automation
-   ✅ Docker containerization
-   ✅ Environment configuration
-   ✅ Database migrations

## 🎓 Project Context

This project was developed as part of a **Full Stack Project Lab - Softserve**, demonstrating:

-   Backend API development with Spring Boot
-   Integration with React frontend
-   Database design and management
-   Security best practices
-   RESTful and GraphQL API design
-   Testing methodologies
-   Professional development workflows
