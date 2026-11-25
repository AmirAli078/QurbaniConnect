package com.qurbaniconnect.exception;

import org.springframework.http.HttpStatus;

public class InvalidPriceException extends ApiException {
    public InvalidPriceException(String message) { super(HttpStatus.UNPROCESSABLE_ENTITY, message); }
}
