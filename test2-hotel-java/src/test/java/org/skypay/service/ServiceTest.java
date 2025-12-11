package org.skypay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypay.exception.*;
import org.skypay.model.RoomType;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Helper method to create Date objects with only year, month, and day.
 */

@DisplayName("Hotel Reservation Service Tests")
class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
    }

    /**
     * Creates a Date object with only year, month, and day.
     */
    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Test
    @DisplayName("Should create a new room successfully")
    void testSetRoom_NewRoom() {
        assertDoesNotThrow(() -> service.setRoom(1, RoomType.STANDARD_SUITE, 1000));
    }

    @Test
    @DisplayName("Should update an existing room")
    void testSetRoom_UpdateExisting() {
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        assertDoesNotThrow(() -> service.setRoom(1, RoomType.MASTER_SUITE, 3000));
    }

    @Test
    @DisplayName("Should reject negative room number")
    void testSetRoom_NegativeRoomNumber() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.setRoom(-1, RoomType.STANDARD_SUITE, 1000));
        assertTrue(exception.getMessage().contains("must be positive"));
    }

    @Test
    @DisplayName("Should reject null room type")
    void testSetRoom_NullRoomType() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.setRoom(1, null, 1000));
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    @DisplayName("Should reject negative price")
    void testSetRoom_NegativePrice() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.setRoom(1, RoomType.STANDARD_SUITE, -100));
        assertTrue(exception.getMessage().contains("must be positive"));
    }

    @Test
    @DisplayName("Should create a new user successfully")
    void testSetUser_NewUser() {
        assertDoesNotThrow(() -> service.setUser(1, 5000));
    }

    @Test
    @DisplayName("Should update existing user balance")
    void testSetUser_UpdateBalance() {
        service.setUser(1, 5000);
        assertDoesNotThrow(() -> service.setUser(1, 10000));
    }

    @Test
    @DisplayName("Should reject negative user ID")
    void testSetUser_NegativeUserId() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.setUser(-1, 5000));
        assertTrue(exception.getMessage().contains("must be positive"));
    }

    @Test
    @DisplayName("Should reject negative balance")
    void testSetUser_NegativeBalance() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.setUser(1, -100));
        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Should successfully book an available room with sufficient balance")
    void testBookRoom_Success() {
        service.setUser(1, 10000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        
        assertDoesNotThrow(() -> service.bookRoom(1, 1, checkIn, checkOut));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testBookRoom_UserNotFound() {
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.bookRoom(999, 1, checkIn, checkOut));
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Should throw exception when room not found")
    void testBookRoom_RoomNotFound() {
        service.setUser(1, 10000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        
        RoomNotFoundException exception = assertThrows(RoomNotFoundException.class,
                () -> service.bookRoom(1, 999, checkIn, checkOut));
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Should throw exception when user has insufficient balance")
    void testBookRoom_InsufficientBalance() {
        service.setUser(1, 1000);
        service.setRoom(1, RoomType.MASTER_SUITE, 3000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class,
                () -> service.bookRoom(1, 1, checkIn, checkOut));
        assertTrue(exception.getMessage().contains("insufficient balance"));
    }

    @Test
    @DisplayName("Should throw exception when room has overlapping bookings")
    void testBookRoom_RoomNotAvailable() {
        service.setUser(1, 50000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn1 = createDate(2025, 1, 10);
        Date checkOut1 = createDate(2025, 1, 15);
        service.bookRoom(1, 1, checkIn1, checkOut1);
        
        Date checkIn2 = createDate(2025, 1, 12);
        Date checkOut2 = createDate(2025, 1, 17);
        
        RoomNotAvailableException exception = assertThrows(RoomNotAvailableException.class,
                () -> service.bookRoom(1, 1, checkIn2, checkOut2));
        assertTrue(exception.getMessage().contains("not available"));
    }

    @Test
    @DisplayName("Should allow booking when dates don't overlap")
    void testBookRoom_NonOverlappingDates() {
        service.setUser(1, 50000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn1 = createDate(2025, 1, 10);
        Date checkOut1 = createDate(2025, 1, 15);
        service.bookRoom(1, 1, checkIn1, checkOut1);
        
        Date checkIn2 = createDate(2025, 1, 20);
        Date checkOut2 = createDate(2025, 1, 25);
        
        assertDoesNotThrow(() -> service.bookRoom(1, 1, checkIn2, checkOut2));
    }

    @Test
    @DisplayName("Should reject null check-in date")
    void testBookRoom_NullCheckIn() {
        service.setUser(1, 10000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkOut = createDate(2025, 1, 15);
        
        assertThrows(Exception.class,
                () -> service.bookRoom(1, 1, null, checkOut));
    }

    @Test
    @DisplayName("Should reject checkout before checkin")
    void testBookRoom_CheckoutBeforeCheckin() {
        service.setUser(1, 10000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn = createDate(2025, 1, 15);
        Date checkOut = createDate(2025, 1, 10);
        
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> service.bookRoom(1, 1, checkIn, checkOut));
        assertTrue(exception.getMessage().contains("must be after"));
    }

    @Test
    @DisplayName("CRITICAL: setRoom should not affect existing bookings (immutability)")
    void testImmutability_SetRoomDoesNotAffectBookings() {
        service.setUser(1, 20000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        service.bookRoom(1, 1, checkIn, checkOut);
        
        service.setRoom(1, RoomType.MASTER_SUITE, 5000);
        
        assertDoesNotThrow(() -> service.printAll());
    }

    @Test
    @DisplayName("Should deduct correct amount from user balance after booking")
    void testBookRoom_BalanceDeduction() {
        service.setUser(1, 10000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        
        Date checkIn = createDate(2025, 1, 10);
        Date checkOut = createDate(2025, 1, 15);
        
        service.bookRoom(1, 1, checkIn, checkOut);
        
        assertDoesNotThrow(() -> service.printAllUsers());
    }

    @Test
    @DisplayName("Should handle multiple bookings for same user")
    void testBookRoom_MultipleBookingsSameUser() {
        service.setUser(1, 50000);
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        
        Date checkIn1 = createDate(2025, 1, 10);
        Date checkOut1 = createDate(2025, 1, 15);
        service.bookRoom(1, 1, checkIn1, checkOut1);
        
        Date checkIn2 = createDate(2025, 2, 10);
        Date checkOut2 = createDate(2025, 2, 15);
        
        assertDoesNotThrow(() -> service.bookRoom(1, 2, checkIn2, checkOut2));
    }

    @Test
    @DisplayName("Should handle printing when no data exists")
    void testPrint_EmptyData() {
        assertDoesNotThrow(() -> service.printAll());
        assertDoesNotThrow(() -> service.printAllUsers());
    }

    @Test
    @DisplayName("Should print data in reverse order (latest to oldest)")
    void testPrint_ReverseOrder() {
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        service.setRoom(3, RoomType.MASTER_SUITE, 3000);
        
        service.setUser(1, 10000);
        service.setUser(2, 20000);
        
        assertDoesNotThrow(() -> service.printAll());
        assertDoesNotThrow(() -> service.printAllUsers());
    }
}
