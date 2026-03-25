# Render and Aiven Deployment Guide

This project uses two deployment modes:

- local development via `docker-compose.yml`
- production deployment on Render with MySQL hosted on Aiven

## Local development

Start the local stack:

```bash
docker compose up --build
```

Local endpoints:

- API: `http://localhost:8081/api/v1`
- MySQL: `localhost:3306`

Local defaults intentionally keep `DEMO_DATA_ENABLED=true` and MySQL SSL disabled.

## Render service contract

Recommended deployment model:

- Runtime: Docker
- Dockerfile: repository `Dockerfile`
- Container port: `8080`
- Health check path: `/api/v1/health`

Required Render environment variables:

- `SPRING_PROFILES_ACTIVE=production`
- `SERVER_PORT=8080`
- `SPRING_DATASOURCE_URL=jdbc:mysql://<aiven-host>:<aiven-port>/<database>?sslMode=REQUIRED&serverTimezone=UTC`
- `SPRING_DATASOURCE_USERNAME=<aiven-username>`
- `SPRING_DATASOURCE_PASSWORD=<aiven-password>`
- `SPRING_FLYWAY_ENABLED=true`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=validate`
- `JWT_SECRET=<base64-or-32-plus-character-secret>`
- `JWT_EXPIRATION=3600000`
- `REFRESH_TOKEN_EXPIRATION=1209600000`
- `EMAIL_VERIFICATION_EXPIRATION=86400000`
- `EMAIL_VERIFICATION_LOG_GENERATED_TOKEN=false`
- `CORS_ALLOWED_ORIGINS=https://<vercel-frontend-domain>`
- `DEMO_DATA_ENABLED=false`

## Aiven MySQL expectations

- Use the host, port, database, username, and password provided by Aiven
- Enforce SSL/TLS in the JDBC URL using the Aiven-provided SSL mode
- Keep credentials out of the repository and Render blueprint files
- Keep Flyway enabled in production so versioned migrations run at startup
- Keep Hibernate on `validate` so production schema changes remain Flyway-managed

## Mapping Aiven details into Render

Map the Aiven service details into Render like this:

- Aiven `Host` -> `SPRING_DATASOURCE_URL` host segment
- Aiven `Port` -> `SPRING_DATASOURCE_URL` port segment
- Aiven `Database Name` -> `SPRING_DATASOURCE_URL` database segment
- Aiven `User` -> `SPRING_DATASOURCE_USERNAME`
- Aiven `Password` -> `SPRING_DATASOURCE_PASSWORD`
- Aiven `SSL Mode` -> `SPRING_DATASOURCE_URL` query parameter `sslMode`

Example format:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://<host>:<port>/<database>?sslMode=REQUIRED&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
```

## First release checklist

- Confirm Aiven database is reachable from Render
- Confirm `SPRING_DATASOURCE_URL` includes SSL mode
- Confirm `SPRING_PROFILES_ACTIVE=production`
- Confirm `DEMO_DATA_ENABLED=false`
- Confirm `EMAIL_VERIFICATION_LOG_GENERATED_TOKEN=false`
- Confirm `CORS_ALLOWED_ORIGINS` matches the Vercel frontend domain
- Confirm the app boots and Flyway completes successfully
- Confirm login works against the deployed API
- Confirm the Vercel frontend can call the deployed API without CORS failures
