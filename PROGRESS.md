# QurbaniConnect Backend – Progress Tracker

Last updated: 2026-11-18. **Resume from "NEXT TASK" below.**

## Environment limits seen so far
The authoring sandbox had Java 21 (JRE + `jdk.compiler` module) but **no Maven, no MySQL, no Docker and no network**.
Consequently: nothing has been built with Maven, Flyway has not run against MySQL, and **no Spring/JUnit tests have run**.

## Verification actually performed
| Check | Result |
|---|---|
| All 83 `.java` files parse with javac (syntax only) | PASS – 0 syntax errors; remaining errors are unresolved Spring/Lombok/JJWT symbols (jars unavailable) |
| `BookingStatus` state machine (standalone compile + run, 12 legal/illegal transitions + terminal states + slot-holding set) | PASS |
| Flyway SQL executed on MySQL 8 | NOT RUN |
| `mvn test` / integration tests / concurrency test | NOT RUN (tests not written yet) |
| Postman collection import | NOT CREATED yet |

## Completed
- [x] pom.xml (Spring Boot 3.3.5, Java 21, JJWT, springdoc, Flyway, Testcontainers)
- [x] application.yml + dev/test/prod profiles (env-var driven, no secrets)
- [x] Dockerfile, docker-compose.yml, .env.example, .gitignore
- [x] Flyway `V1__create_schema.sql` (21 tables incl. `refresh_tokens`), `V2__seed_reference_data.sql`
- [x] DB-level double-booking guard: `bookings.active_availability_id` generated column + UNIQUE index
- [x] Enums, all 20 JPA entities, 19 repositories (pessimistic-lock finders for slot/booking/qasai)
- [x] Exceptions + `GlobalExceptionHandler`, `ApiResponse`, `PageResponse`
- [x] Security: JWT service, filter, `SecurityConfig` (role-based URL rules, JSON 401/403, CORS), BCrypt(12), `AdminSeeder`
- [x] Auth module: register customer/qasai, login (email or phone), rotating hashed refresh tokens, logout, forgot/reset (hashed tokens), `/me`
- [x] Swagger/OpenAPI config with Bearer scheme

## NEXT TASK (in order)
1. **Get a build environment** (enable network for Maven Central or run locally), then `mvn -q compile` and fix any compile errors in the existing 83 files; run the app against MySQL 8 and confirm Flyway migrates (watch: CHECK + generated-column + FK rules, `Notification.read` JPQL name).
2. Location, AnimalCategory, PriceLimit, Pricing (BigDecimal, limit validation) services + admin/public controllers
3. FileStorage abstraction + Qasai profile / documents (MIME + extension + size validation, private dir, admin/owner download)
4. Availability service (overlap check, block/update/delete, slot lock)
5. Booking service: direct + admin-assisted, price snapshot, transitions via `BookingStatus`, status history, slot reserve/release with `findByIdForUpdate`
6. Assignment (assign/reassign), Qasai accept/reject/complete, cancel
7. Review, Notification services/controllers
8. Customer/Qasai/Admin dashboards, admin customer/qasai/booking/review management, public search (Specification/JPQL + pagination)
9. Unit tests (Mockito) + integration tests (Testcontainers MySQL) incl. concurrent double-booking test
10. Postman collection + environment (valid JSON, scripts, e2e order), README execution order
11. docs/api-documentation.md, final completion report

## Not started (counts against acceptance criteria)
Everything under "NEXT TASK" 2–11. Acceptance criteria are **not yet met**.
