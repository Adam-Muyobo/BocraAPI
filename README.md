# BOCRA API

Production-minded Spring Boot API foundation for BOCRA. The project is structured for a multi-developer hackathon team that needs clear module boundaries, secure JWT authentication, MySQL persistence, and deployment-ready configuration for Render with a remote MySQL instance such as Aiven.

## Tech Stack

- Java 21
- Spring Boot 4.0.4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Flyway
- MySQL
- JWT with JJWT
- Lombok
- Docker

## Package Structure

```text
src/main/java/bw/org/bocra/api
├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── common
│   ├── dto
│   └── entity
├── config
├── exception
├── person
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── repository
│   └── service
├── security
│   ├── handler
│   └── jwt
└── user
    ├── controller
    ├── dto
    ├── entity
    ├── enums
    ├── repository
    └── service
```

Each domain package owns its own `entity`, `repository`, `service`, `controller`, `dto`, and `enums` layers. This pattern should be repeated for future BOCRA modules so teams can work in parallel without mixing unrelated responsibilities.

## Identity Model

### `User`

The `user` module owns authentication and authorization data:

- UUID primary identifier
- email login
- optional username
- BCrypt password hash
- role enum
- account status enum
- account state flags used by Spring Security
- email verification timestamp
- last login timestamp
- audit timestamps

### `Person`

The `person` module owns BOCRA profile and identity data:

- UUID primary identifier
- names and date of birth
- gender and nationality
- national ID type and identity numbers
- phones and residential address
- occupation and organization details
- profile photo URL
- audit timestamps

`User` and `Person` are linked one-to-one. `User` is responsible for access control, while `Person` carries detailed identity information used by BOCRA workflows.

## Authentication and Authorization

The API uses stateless JWT authentication with Spring Security:

- `POST /api/v1/auth/register` creates a new applicant user and linked person profile, then generates an email verification token
- `POST /api/v1/auth/verify-email` activates a newly registered account
- `POST /api/v1/auth/resend-verification` invalidates previous verification tokens and issues a new one
- `POST /api/v1/auth/login` authenticates by email and password after the email address is verified
- `POST /api/v1/auth/refresh` rotates an opaque refresh token and returns a fresh JWT access token pair
- `POST /api/v1/auth/logout` revokes a refresh token
- `GET /api/v1/auth/me` returns the currently authenticated user snapshot
- `GET /api/v1/users/me` returns the authenticated user plus linked person profile
- `GET /api/v1/users/{userUuid}` is restricted to `ADMIN` and `SUPER_ADMIN`
- `GET /api/v1/persons/me` and `PUT /api/v1/persons/me` require authentication

JWT secrets are never hardcoded in source. The application reads them from environment variables.

### Verification and Refresh Token Flow

- Registration creates the `User` in `PENDING_VERIFICATION` state and stores a hashed email verification token in the database
- Local development can log the generated verification token by setting `EMAIL_VERIFICATION_LOG_GENERATED_TOKEN=true`
- Verification marks `emailVerifiedAt`, enables the account, and switches the status to `ACTIVE`
- Refresh tokens are opaque random values, hashed before persistence, rotated on refresh, and revocable on logout
- Access tokens remain stateless JWTs and are intentionally short lived compared to refresh tokens

## Environment Variables

The app reads configuration from environment variables through `src/main/resources/application.yml`.

Important variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_FLYWAY_ENABLED`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `REFRESH_TOKEN_EXPIRATION`
- `EMAIL_VERIFICATION_EXPIRATION`
- `EMAIL_VERIFICATION_LOG_GENERATED_TOKEN`
- `SERVER_PORT`
- `CORS_ALLOWED_ORIGINS`

### Env Files

- `.env.example`: safe template for onboarding and documentation; commit this file
- `.env.local`: local developer convenience file; do not commit real values

`application.yml` imports `.env.local` if present, which makes local setup easier while still keeping production configuration environment-driven.

## Running Locally

1. Install Java 21 and ensure MySQL is available locally or point to a remote development database.
2. Copy values from `.env.example` into `.env.local` and replace placeholders with your local settings.
3. Create a JWT secret that is at least 32 characters long, or use a long Base64-encoded value.
4. Leave `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` so entity mappings are checked against Flyway-managed schema.
5. Start the application:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The API base path is `/api/v1`.

On local environments, Flyway automatically runs `src/main/resources/db/migration/V1__initial_security_and_identity_schema.sql` before Hibernate validates the mappings.

## Connecting to MySQL

### Local MySQL

Use a datasource URL like:

```text
jdbc:mysql://localhost:3306/bocra_api?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Aiven MySQL

Use the connection details from Aiven and set them only through environment variables. A typical datasource URL will include the Aiven host, port, database name, SSL options, and credentials supplied by Aiven. Do not store those values in the repository.

Example pattern:

```text
jdbc:mysql://<aiven-host>:<aiven-port>/<database>?sslMode=REQUIRED&serverTimezone=UTC
```

## Render Deployment Model

This repository assumes:

- the Spring Boot API is deployed on Render
- MySQL is hosted remotely on Aiven
- Render injects environment variables at deploy time

Recommended Render settings:

- build command: `./mvnw clean package -DskipTests`
- start command: `java -jar target/api-0.0.1-SNAPSHOT.jar`
- set the datasource and JWT variables in the Render dashboard
- keep `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` in Render so production never mutates schema implicitly

## Docker

The included `Dockerfile` containerizes only the Spring Boot API, which matches the intended deployment model where MySQL is hosted separately.

Build and run locally:

```bash
docker build -t bocra-api .
docker run --env-file .env.local -p 8080:8080 bocra-api
```

## Security Notes

- Never commit real secrets in `.env.local`, source files, or `README.md`
- Keep `.env.example` as placeholders only
- Prefer Render environment variables for deployed environments
- Rotate `JWT_SECRET` if it is ever exposed
- Use HTTPS in production and enforce SSL when connecting to Aiven
- Keep `EMAIL_VERIFICATION_LOG_GENERATED_TOKEN=false` outside local development
- Integrate a real email provider behind the verification notification service before production launch

## Entity Package Pattern For Future Modules

Every future BOCRA entity package should follow the same structure:

```text
module-name/
├── controller
├── dto
├── entity
├── enums
├── repository
└── service
```

Recommended rules for new modules:

- keep controllers thin and HTTP-focused
- put business rules in services
- use DTOs for requests and responses
- keep repositories persistence-only
- use UUIDs consistently in APIs and service contracts
- use Flyway migrations for every schema change before updating entities
- give each new auth-related workflow its own package-local entity/repository/service when it has persistence of its own
- place shared concerns in `common`, `config`, `security`, and `exception`

## Current Notes

- The project now uses Flyway for schema creation and keeps Hibernate on `validate` by default
- Email verification delivery is abstracted behind `VerificationNotificationService`; the default implementation logs tokens only when explicitly enabled for local development
- Refresh tokens are persisted as SHA-256 hashes, not plain values
