package org.skypay.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private final int bookingId;
    private final RoomSnapshot roomSnapshot;
    private final UserSnapshot userSnapshot;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int numberOfNights;
    private final int totalCost;
    private final LocalDate bookingDate;

    public Booking(int bookingId, Room room, User user, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.roomSnapshot = new RoomSnapshot(room);
        this.userSnapshot = new UserSnapshot(user);
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfNights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalCost = numberOfNights * room.getPricePerNight();
        this.bookingDate = LocalDate.now();
    }

    public int getBookingId() {
        return bookingId;
    }

    public RoomSnapshot getRoomSnapshot() {
        return roomSnapshot;
    }

    public UserSnapshot getUserSnapshot() {
        return userSnapshot;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public boolean hasDateOverlap(LocalDate newCheckIn, LocalDate newCheckOut) {
        return newCheckIn.isBefore(checkOut) && newCheckOut.isAfter(checkIn);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", room=" + roomSnapshot +
                ", user=" + userSnapshot +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", nights=" + numberOfNights +
                ", totalCost=" + totalCost +
                '}';
    }
}
