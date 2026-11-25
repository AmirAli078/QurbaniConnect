# Business Workflows & Rules

## Booking status machine (implemented in `BookingStatus`, unit-verified)
```
PENDING   -> ASSIGNED | CONFIRMED | REJECTED | CANCELLED
ASSIGNED  -> CONFIRMED | PENDING (assignee rejected, awaiting reassignment) | ASSIGNED (reassign) | CANCELLED
CONFIRMED -> COMPLETED | CANCELLED
REJECTED, COMPLETED, CANCELLED are terminal.
```
`ACCEPTED` exists in the schema for compatibility, but accepting a request moves it directly to `CONFIRMED`.

## Slot reservation policy
- A booking **holds** its availability slot while in PENDING, ACCEPTED, ASSIGNED or CONFIRMED.
- Creating a direct booking locks the slot row (`SELECT ... FOR UPDATE`), requires `AVAILABLE`, sets it `RESERVED`.
- REJECTED / CANCELLED releases the slot back to `AVAILABLE` (unless the Qasai blocked it meanwhile). COMPLETED keeps it `RESERVED`.
- Backstop: `bookings.active_availability_id` is a generated column that equals `availability_id` only while the status holds a slot, with a UNIQUE index. Even a bug or a race in the application cannot create two active bookings for one slot; the loser gets HTTP 409.

## Pricing
- Client never sends prices. Unit prices are read from `qasai_animal_prices` at booking time and **snapshotted** in `booking_items` (with animal name). Later price edits do not alter old bookings.
- `subtotal = quantity × unit_price`, total = sum of subtotals, all `BigDecimal` / `DECIMAL`, scale 2, `HALF_UP`.
- A Qasai price must be > 0, for an active animal, within the admin min/max limit (active limit required).
- **Admin-assisted estimate policy:** no Qasai is chosen yet, so the estimate uses the **midpoint of the active admin price limit** (`(min+max)/2`) per animal, and the booking is flagged `price_status = ESTIMATED`. On assignment, items are re-priced from the assigned Qasai's rates and the status becomes `FINAL`. An assignment is refused if that Qasai has not priced every requested animal.

## Security rules
- Public registration only creates CUSTOMER or QASAI. Admin comes from env seed (`ADMIN_EMAIL`, `ADMIN_PASSWORD`).
- Verification documents are stored outside static paths; never returned by public APIs; downloadable only by admin and the owning Qasai. CNIC is never in public DTOs.
- Refresh tokens and password-reset tokens are stored as SHA-256 hashes; refresh tokens rotate and are revocable.
