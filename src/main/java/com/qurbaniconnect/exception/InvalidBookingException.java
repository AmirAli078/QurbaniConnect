package com.qurbaniconnect.exception;

import org.springframework.http.HttpStatus;

public class InvalidBookingException extends ApiException {
    public InvalidBookingException(String message) { super(HttpStatus.UNPROCESSABLE_ENTITY, message); }
}
