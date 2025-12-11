package org.skypay.test2.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Booking entity.
 */
class BookingTest {

    @Test
    void testBookingCreation_Valid() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        Booking booking = new Booking(1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut);
        
        assertNotNull(booking);
        assertEquals(1, booking.getUserId());
        assertEquals(101, booking.getRoomNumber());
        assertEquals(checkIn, booking.getCheckIn());
        assertEquals(checkOut, booking.getCheckOut());
        assertEquals(3, booking.getNumberOfNights());
        assertEquals(3000, booking.getTotalPrice());
        assertEquals(5000, booking.getUserBalanceAtBooking());
        assertEquals(RoomType.STANDARD, booking.getRoomTypeAtBooking());
        assertEquals(1000, booking.getRoomPricePerNightAtBooking());
    }

    @Test
    void testBookingCreation_InvalidUserId() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(0, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut));
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(-1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut));
    }

    @Test
    void testBookingCreation_InvalidRoomNumber() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 0, RoomType.STANDARD, 1000, checkIn, checkOut));
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, -1, RoomType.STANDARD, 1000, checkIn, checkOut));
    }

    @Test
    void testBookingCreation_NullDates() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, 1000, null, LocalDate.of(2026, 7, 10)));
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, 1000, LocalDate.of(2026, 7, 7), null));
    }

    @Test
    void testBookingCreation_InvalidDates() {
        LocalDate checkIn = LocalDate.of(2026, 7, 10);
        LocalDate checkOut = LocalDate.of(2026, 7, 7);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut));
    }

    @Test
    void testBookingCreation_SameDates() {
        LocalDate date = LocalDate.of(2026, 7, 7);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, 1000, date, date));
    }

    @Test
    void testBookingCreation_NullRoomType() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, null, 1000, checkIn, checkOut));
    }

    @Test
    void testBookingCreation_InvalidPrice() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, 0, checkIn, checkOut));
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, 5000, 101, RoomType.STANDARD, -100, checkIn, checkOut));
    }

    @Test
    void testBookingCreation_NegativeBalance() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        assertThrows(IllegalArgumentException.class, () -> 
            new Booking(1, -1000, 101, RoomType.STANDARD, 1000, checkIn, checkOut));
    }

    @Test
    void testOverlaps_FullyOverlapping() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        Booking booking = new Booking(1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut);
        
        assertTrue(booking.overlaps(LocalDate.of(2026, 7, 7), LocalDate.of(2026, 7, 10)));
        assertTrue(booking.overlaps(LocalDate.of(2026, 7, 8), LocalDate.of(2026, 7, 9)));
        assertTrue(booking.overlaps(LocalDate.of(2026, 7, 6), LocalDate.of(2026, 7, 11)));
        assertTrue(booking.overlaps(LocalDate.of(2026, 7, 6), LocalDate.of(2026, 7, 8)));
        assertTrue(booking.overlaps(LocalDate.of(2026, 7, 9), LocalDate.of(2026, 7, 11)));
    }

    @Test
    void testOverlaps_NoOverlap() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        Booking booking = new Booking(1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut);
        
        // Before
        assertFalse(booking.overlaps(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 7)));
        
        // After
        assertFalse(booking.overlaps(LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15)));
    }

    @Test
    void testBookingIdIncrement() {
        LocalDate checkIn = LocalDate.of(2026, 7, 7);
        LocalDate checkOut = LocalDate.of(2026, 7, 10);
        
        Booking booking1 = new Booking(1, 5000, 101, RoomType.STANDARD, 1000, checkIn, checkOut);
        Booking booking2 = new Booking(2, 10000, 102, RoomType.JUNIOR, 2000, checkIn, checkOut);
        
        assertTrue(booking2.getBookingId() > booking1.getBookingId());
    }
}
