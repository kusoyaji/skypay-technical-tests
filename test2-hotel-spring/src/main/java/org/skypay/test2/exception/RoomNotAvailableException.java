package org.skypay.test2.exception;

/**
 * Exception thrown when a room is not available for booking.
 */
public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
