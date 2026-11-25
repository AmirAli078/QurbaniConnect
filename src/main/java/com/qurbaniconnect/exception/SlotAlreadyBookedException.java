package com.qurbaniconnect.exception;

import org.springframework.http.HttpStatus;

public class SlotAlreadyBookedException extends ApiException {
    public SlotAlreadyBookedException(String message) { super(HttpStatus.CONFLICT, message); }
}
