package org.skypay.test2.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a booking in the hotel reservation system.
 * This entity stores snapshots of room and user data at the time of booking
 * to ensure historical data is preserved even if room/user details change later.
 */
public class Booking {
    private static int bookingIdCounter = 0;
    
    private final int bookingId;
    private final int userId;
    private final int roomNumber;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final long numberOfNights;
    private final int totalPrice;
    
    // Snapshots of room and user data at booking time
    private final int userBalanceAtBooking;
    private final RoomType roomTypeAtBooking;
    private final int roomPricePerNightAtBooking;
    
    private final LocalDateTime createdAt;

    public Booking(int userId, int userBalance, int roomNumber, RoomType roomType, 
                   int roomPricePerNight, LocalDate checkIn, LocalDate checkOut) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (roomPricePerNight <= 0) {
            throw new IllegalArgumentException("Room price per night must be positive");
        }
        if (userBalance < 0) {
            throw new IllegalArgumentException("User balance cannot be negative");
        }

        this.bookingId = ++bookingIdCounter;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalPrice = (int) (numberOfNights * roomPricePerNight);
        
        // Store snapshots
        this.userBalanceAtBooking = userBalance;
        this.roomTypeAtBooking = roomType;
        this.roomPricePerNightAtBooking = roomPricePerNight;
        
        this.createdAt = LocalDateTime.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getUserBalanceAtBooking() {
        return userBalanceAtBooking;
    }

    public RoomType getRoomTypeAtBooking() {
        return roomTypeAtBooking;
    }

    public int getRoomPricePerNightAtBooking() {
        return roomPricePerNightAtBooking;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if this booking overlaps with a given date range.
     */
    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return !checkOut.isBefore(otherCheckIn) && !otherCheckOut.isBefore(checkIn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", roomNumber=" + roomNumber +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", numberOfNights=" + numberOfNights +
                ", totalPrice=" + totalPrice +
                ", userBalanceAtBooking=" + userBalanceAtBooking +
                ", roomTypeAtBooking=" + roomTypeAtBooking +
                ", roomPricePerNightAtBooking=" + roomPricePerNightAtBooking +
                ", createdAt=" + createdAt +
                '}';
    }
}
