# QurbaniConnect Backend
Find. Book. Connect. – Qasai booking marketplace (Pakistan, PKR). Java 21, Spring Boot 3.3, MySQL 8.

> **Status: work in progress – see `PROGRESS.md`.** Foundation (schema, entities, security, auth) is written; booking/pricing/admin modules, tests and Postman collection are pending. Not yet compiled with Maven.

## Run locally
```bash
cp .env.example .env            # edit DB_PASSWORD, JWT_SECRET (>=32 chars), ADMIN_EMAIL, ADMIN_PASSWORD
export $(grep -v '^#' .env | xargs)
mvn spring-boot:run             # Flyway creates the schema in qurbaniconnect_db
```
Swagger: http://localhost:8080/swagger-ui/index.html · Health: /actuator/health

## Docker
`docker compose --env-file .env up --build`

## Admin bootstrap
The first admin is created at startup from `ADMIN_EMAIL` / `ADMIN_PASSWORD` if no admin exists. There is no public admin registration.
