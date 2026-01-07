package com.superhealthclaim.claimservice.exception;

public class ClaimNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClaimNotFoundException(String message) {
        super(message);
    }

    public ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
