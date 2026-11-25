package com.qurbaniconnect.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Booking state machine. Every status change must go through {@link #canTransitionTo}.
 *
 * Direct flow:         PENDING -> CONFIRMED | REJECTED | CANCELLED ; CONFIRMED -> COMPLETED | CANCELLED
 * Admin-assisted flow: PENDING -> ASSIGNED -> CONFIRMED | PENDING (assignee rejected, awaiting reassignment)
 * ACCEPTED is kept in the schema for compatibility; acceptance moves a booking straight to CONFIRMED.
 */
public enum BookingStatus {
    PENDING, ACCEPTED, REJECTED, ASSIGNED, CONFIRMED, COMPLETED, CANCELLED;

    /** Statuses in which a booking occupies its availability slot (mirrors the DB generated column). */
    public static final Set<BookingStatus> SLOT_HOLDING = EnumSet.of(PENDING, ACCEPTED, ASSIGNED, CONFIRMED);

    public boolean canTransitionTo(BookingStatus target) {
        return switch (this) {
            case PENDING -> EnumSet.of(ASSIGNED, CONFIRMED, REJECTED, CANCELLED).contains(target);
            case ACCEPTED -> EnumSet.of(CONFIRMED, CANCELLED).contains(target);
            case ASSIGNED -> EnumSet.of(CONFIRMED, PENDING, ASSIGNED, CANCELLED).contains(target);
            case CONFIRMED -> EnumSet.of(COMPLETED, CANCELLED).contains(target);
            case REJECTED, COMPLETED, CANCELLED -> false;
        };
    }

    public boolean holdsSlot() { return SLOT_HOLDING.contains(this); }
}
