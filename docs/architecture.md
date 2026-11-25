# Architecture
Layered Spring Boot 3 / Java 21 application: `controller` (HTTP only) → `service` (+`impl`, business rules, `@Transactional`) → `repository` (Spring Data JPA) → MySQL 8.
DTOs (Java records) cross the API boundary; entities are never serialized. `mapper` converts entity→DTO. `security` holds JWT + filter; `exception` holds the typed exceptions and `GlobalExceptionHandler`.
All responses use `ApiResponse{success,message,data,timestamp}`. Stateless JWT auth; roles `ROLE_CUSTOMER|ROLE_QASAI|ROLE_ADMIN` enforced by URL prefix in `SecurityConfig` and re-checked for ownership in services.
Concurrency: pessimistic row locks (`findByIdForUpdate`) plus a DB unique index on active slot reservations.
Storage: `FileStorageService` abstraction (local private directory now, object storage later). Notifications: DB-backed, behind a service so email/WhatsApp can be added later.
