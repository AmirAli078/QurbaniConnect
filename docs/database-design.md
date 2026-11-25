# Database Design (MySQL 8.0, InnoDB, utf8mb4)

Schema is owned by Flyway (`src/main/resources/db/migration`). Hibernate runs with `ddl-auto=validate`.

```mermaid
erDiagram
  users ||--o{ user_roles : has
  roles ||--o{ user_roles : grants
  users ||--o| customer_profiles : "is"
  users ||--o| qasai_profiles : "is"
  users ||--o{ notifications : receives
  users ||--o{ refresh_tokens : owns
  users ||--o{ password_reset_tokens : owns
  cities ||--o{ areas : contains
  cities ||--o{ customer_profiles : located
  cities ||--o{ qasai_profiles : located
  qasai_profiles ||--o{ qasai_verification_documents : submits
  qasai_profiles ||--o{ qasai_animal_prices : sets
  animal_categories ||--o{ qasai_animal_prices : priced
  animal_categories ||--|| admin_price_limits : limited
  qasai_profiles ||--o{ qasai_availability : offers
  customer_profiles ||--o{ bookings : places
  qasai_profiles ||--o{ bookings : fulfils
  qasai_availability ||--o{ bookings : reserved_by
  bookings ||--|{ booking_items : contains
  animal_categories ||--o{ booking_items : of
  bookings ||--o{ booking_status_history : logs
  bookings ||--o{ qasai_assignments : assigned
  bookings ||--o| reviews : reviewed
```

Key constraints: unique email/phone/CNIC; `UNIQUE(qasai_id, animal_category_id)` on prices; `CHECK service_price > 0`;
`CHECK subtotal = quantity * unit_price`; `UNIQUE(active_availability_id)` (generated) for double-booking prevention;
`UNIQUE(booking_id)` on reviews; `CHECK rating BETWEEN 1 AND 5`.
