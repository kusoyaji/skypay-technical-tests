package org.skypay.test2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypay.test2.exception.InsufficientBalanceException;
import org.skypay.test2.exception.RoomNotAvailableException;
import org.skypay.test2.exception.RoomNotFoundException;
import org.skypay.test2.exception.UserNotFoundException;
import org.skypay.test2.model.Booking;
import org.skypay.test2.model.Room;
import org.skypay.test2.model.RoomType;
import org.skypay.test2.model.User;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Service class.
 */
class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    // Helper method to create dates
    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // Tests for setRoom
    @Test
    void testSetRoom_CreatesNewRoom() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        
        assertEquals(1, service.getRooms().size());
        Room room = service.getRooms().get(0);
        assertEquals(1, room.getRoomNumber());
        assertEquals(RoomType.STANDARD, room.getRoomType());
        assertEquals(1000, room.getPricePerNight());
    }

    @Test
    void testSetRoom_UpdatesExistingRoom() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(1, RoomType.MASTER, 5000);
        
        assertEquals(1, service.getRooms().size());
        Room room = service.getRooms().get(0);
        assertEquals(RoomType.MASTER, room.getRoomType());
        assertEquals(5000, room.getPricePerNight());
    }

    @Test
    void testSetRoom_InvalidRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.setRoom(0, RoomType.STANDARD, 1000));
        assertThrows(IllegalArgumentException.class, () -> 
            service.setRoom(-1, RoomType.STANDARD, 1000));
    }

    @Test
    void testSetRoom_NullRoomType() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.setRoom(1, null, 1000));
    }

    @Test
    void testSetRoom_InvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.setRoom(1, RoomType.STANDARD, 0));
        assertThrows(IllegalArgumentException.class, () -> 
            service.setRoom(1, RoomType.STANDARD, -100));
    }

    // Tests for setUser
    @Test
    void testSetUser_CreatesNewUser() {
        service.setUser(1, 5000);
        
        assertEquals(1, service.getUsers().size());
        User user = service.getUsers().get(0);
        assertEquals(1, user.getUserId());
        assertEquals(5000, user.getBalance());
    }

    @Test
    void testSetUser_UpdatesExistingUser() {
        service.setUser(1, 5000);
        service.setUser(1, 10000);
        
        assertEquals(1, service.getUsers().size());
        User user = service.getUsers().get(0);
        assertEquals(10000, user.getBalance());
    }

    @Test
    void testSetUser_InvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.setUser(0, 5000));
        assertThrows(IllegalArgumentException.class, () -> 
            service.setUser(-1, 5000));
    }

    @Test
    void testSetUser_NegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.setUser(1, -100));
    }

    // Tests for bookRoom
    @Test
    void testBookRoom_SuccessfulBooking() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        assertEquals(1, service.getBookings().size());
        Booking booking = service.getBookings().get(0);
        assertEquals(1, booking.getUserId());
        assertEquals(1, booking.getRoomNumber());
        assertEquals(1, booking.getNumberOfNights());
        assertEquals(1000, booking.getTotalPrice());
        
        // Check user balance was deducted
        User user = service.getUsers().get(0);
        assertEquals(4000, user.getBalance());
    }

    @Test
    void testBookRoom_InsufficientBalance() {
        service.setRoom(1, RoomType.MASTER, 5000);
        service.setUser(1, 3000);
        
        assertThrows(InsufficientBalanceException.class, () -> 
            service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8)));
    }

    @Test
    void testBookRoom_RoomNotFound() {
        service.setUser(1, 5000);
        
        assertThrows(RoomNotFoundException.class, () -> 
            service.bookRoom(1, 999, createDate(2026, 7, 7), createDate(2026, 7, 8)));
    }

    @Test
    void testBookRoom_UserNotFound() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        
        assertThrows(UserNotFoundException.class, () -> 
            service.bookRoom(999, 1, createDate(2026, 7, 7), createDate(2026, 7, 8)));
    }

    @Test
    void testBookRoom_InvalidDates() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        
        // Check-out before check-in
        assertThrows(IllegalArgumentException.class, () -> 
            service.bookRoom(1, 1, createDate(2026, 7, 10), createDate(2026, 7, 5)));
        
        // Check-out equals check-in
        assertThrows(IllegalArgumentException.class, () -> 
            service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 7)));
    }

    @Test
    void testBookRoom_RoomNotAvailable_OverlappingDates() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 10000);
        service.setUser(2, 10000);
        
        // First booking: 7-8 July
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        // Try to book same room for overlapping period
        assertThrows(RoomNotAvailableException.class, () -> 
            service.bookRoom(2, 1, createDate(2026, 7, 7), createDate(2026, 7, 9)));
    }

    @Test
    void testBookRoom_ConsecutiveBookings_ShouldSucceed() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 10000);
        service.setUser(2, 10000);
        
        // First booking: 7-8 July (check-out on 8th)
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        // Second booking: 8-9 July (check-in on 8th) - should succeed
        service.bookRoom(2, 1, createDate(2026, 7, 8), createDate(2026, 7, 9));
        
        assertEquals(2, service.getBookings().size());
    }

    @Test
    void testBookRoom_MultipleNights() {
        service.setRoom(1, RoomType.JUNIOR, 2000);
        service.setUser(1, 20000);
        
        service.bookRoom(1, 1, createDate(2026, 6, 30), createDate(2026, 7, 7));
        
        Booking booking = service.getBookings().get(0);
        assertEquals(7, booking.getNumberOfNights());
        assertEquals(14000, booking.getTotalPrice());
        
        User user = service.getUsers().get(0);
        assertEquals(6000, user.getBalance());
    }

    // Tests for setRoom not impacting existing bookings
    @Test
    void testSetRoom_DoesNotImpactExistingBookings() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        
        // Create a booking
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        Booking bookingBefore = service.getBookings().get(0);
        assertEquals(RoomType.STANDARD, bookingBefore.getRoomTypeAtBooking());
        assertEquals(1000, bookingBefore.getRoomPricePerNightAtBooking());
        assertEquals(1000, bookingBefore.getTotalPrice());
        
        // Update the room
        service.setRoom(1, RoomType.MASTER, 10000);
        
        // Check that booking still has original values
        Booking bookingAfter = service.getBookings().get(0);
        assertEquals(RoomType.STANDARD, bookingAfter.getRoomTypeAtBooking());
        assertEquals(1000, bookingAfter.getRoomPricePerNightAtBooking());
        assertEquals(1000, bookingAfter.getTotalPrice());
        
        // But the room itself is updated
        Room room = service.getRooms().get(0);
        assertEquals(RoomType.MASTER, room.getRoomType());
        assertEquals(10000, room.getPricePerNight());
    }

    @Test
    void testBooking_StoresUserBalanceSnapshot() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        Booking booking = service.getBookings().get(0);
        assertEquals(5000, booking.getUserBalanceAtBooking());
        
        // User balance after booking
        User user = service.getUsers().get(0);
        assertEquals(4000, user.getBalance());
    }

    // Test full scenario from requirements
    @Test
    void testCompleteScenario() {
        // Create 3 rooms
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setRoom(2, RoomType.JUNIOR, 2000);
        service.setRoom(3, RoomType.MASTER, 3000);
        
        // Create 2 users
        service.setUser(1, 5000);
        service.setUser(2, 10000);
        
        // User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights = 14000)
        // Should fail - insufficient balance
        assertThrows(InsufficientBalanceException.class, () -> 
            service.bookRoom(1, 2, createDate(2026, 6, 30), createDate(2026, 7, 7)));
        
        // User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026
        // Should fail - invalid dates
        assertThrows(IllegalArgumentException.class, () -> 
            service.bookRoom(1, 2, createDate(2026, 7, 7), createDate(2026, 6, 30)));
        
        // User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night = 1000)
        // Should succeed
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        assertEquals(1, service.getBookings().size());
        assertEquals(4000, service.getUsers().get(0).getBalance());
        
        // User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights)
        // Should fail - room not available
        assertThrows(RoomNotAvailableException.class, () -> 
            service.bookRoom(2, 1, createDate(2026, 7, 7), createDate(2026, 7, 9)));
        
        // User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night = 3000)
        // Should succeed
        service.bookRoom(2, 3, createDate(2026, 7, 7), createDate(2026, 7, 8));
        assertEquals(2, service.getBookings().size());
        assertEquals(7000, service.getUsers().get(1).getBalance());
        
        // setRoom(1, MASTER, 10000)
        service.setRoom(1, RoomType.MASTER, 10000);
        
        // Verify room is updated
        Room room1 = service.getRooms().stream()
            .filter(r -> r.getRoomNumber() == 1)
            .findFirst()
            .orElse(null);
        assertNotNull(room1);
        assertEquals(RoomType.MASTER, room1.getRoomType());
        assertEquals(10000, room1.getPricePerNight());
        
        // Verify booking still has original values
        Booking booking1 = service.getBookings().stream()
            .filter(b -> b.getRoomNumber() == 1)
            .findFirst()
            .orElse(null);
        assertNotNull(booking1);
        assertEquals(RoomType.STANDARD, booking1.getRoomTypeAtBooking());
        assertEquals(1000, booking1.getRoomPricePerNightAtBooking());
    }

    @Test
    void testPrintAll_DoesNotThrowException() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        assertDoesNotThrow(() -> service.printAll());
    }

    @Test
    void testPrintAllUsers_DoesNotThrowException() {
        service.setUser(1, 5000);
        service.setUser(2, 10000);
        
        assertDoesNotThrow(() -> service.printAllUsers());
    }

    @Test
    void testClearAll() {
        service.setRoom(1, RoomType.STANDARD, 1000);
        service.setUser(1, 5000);
        service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
        
        service.clearAll();
        
        assertEquals(0, service.getRooms().size());
        assertEquals(0, service.getUsers().size());
        assertEquals(0, service.getBookings().size());
    }
}
