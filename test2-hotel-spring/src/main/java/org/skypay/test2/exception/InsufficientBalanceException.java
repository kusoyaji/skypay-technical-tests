package org.skypay.test2.exception;

/**
 * Exception thrown when a user has insufficient balance for a booking.
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
