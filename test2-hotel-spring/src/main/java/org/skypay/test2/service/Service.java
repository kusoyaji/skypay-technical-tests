package org.skypay.test2.service;

import org.skypay.test2.exception.InsufficientBalanceException;
import org.skypay.test2.exception.RoomNotAvailableException;
import org.skypay.test2.exception.RoomNotFoundException;
import org.skypay.test2.exception.UserNotFoundException;
import org.skypay.test2.model.Booking;
import org.skypay.test2.model.Room;
import org.skypay.test2.model.RoomType;
import org.skypay.test2.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing hotel reservations.
 * Uses ArrayLists instead of repositories as per requirements.
 */
@Component
public class Service {
    
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Booking> bookings = new ArrayList<>();

    /**
     * Sets or updates a room. Creates the room if it doesn't exist.
     * Updates the room if it already exists.
     * This operation does NOT impact previously created bookings.
     */
    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }
        if (roomPricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be positive");
        }

        Room existingRoom = findRoomByNumber(roomNumber);
        if (existingRoom != null) {
            // Update existing room
            existingRoom.setRoomType(roomType);
            existingRoom.setPricePerNight(roomPricePerNight);
        } else {
            // Create new room
            rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
        }
    }

    /**
     * Books a room for a user for a specific period.
     * Validates that the user has enough balance and the room is available.
     */
    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        // Validate inputs
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }

        // Convert Date to LocalDate (only year, month, day)
        LocalDate checkInDate = convertToLocalDate(checkIn);
        LocalDate checkOutDate = convertToLocalDate(checkOut);

        // Validate dates
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Find user
        User user = findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }

        // Find room
        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " not found");
        }

        // Check if room is available for the period
        if (!isRoomAvailable(roomNumber, checkInDate, checkOutDate)) {
            throw new RoomNotAvailableException(
                "Room " + roomNumber + " is not available from " + checkInDate + " to " + checkOutDate
            );
        }

        // Calculate total price
        long numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        int totalPrice = (int) (numberOfNights * room.getPricePerNight());

        // Check if user has enough balance
        if (user.getBalance() < totalPrice) {
            throw new InsufficientBalanceException(
                "User " + userId + " has insufficient balance. Required: " + totalPrice + 
                ", Available: " + user.getBalance()
            );
        }

        // Create booking with snapshots of current room and user data
        Booking booking = new Booking(
            userId,
            user.getBalance(),
            roomNumber,
            room.getRoomType(),
            room.getPricePerNight(),
            checkInDate,
            checkOutDate
        );

        // Deduct balance and add booking
        user.deductBalance(totalPrice);
        bookings.add(booking);
    }

    /**
     * Prints all rooms and bookings from latest to oldest.
     */
    public void printAll() {
        System.out.println("=== ALL ROOMS (Latest to Oldest) ===");
        List<Room> sortedRooms = rooms.stream()
            .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        if (sortedRooms.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            for (Room room : sortedRooms) {
                System.out.println("Room Number: " + room.getRoomNumber() + 
                                 ", Type: " + room.getRoomType().getDisplayName() + 
                                 ", Price/Night: " + room.getPricePerNight());
            }
        }

        System.out.println("\n=== ALL BOOKINGS (Latest to Oldest) ===");
        List<Booking> sortedBookings = bookings.stream()
            .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        if (sortedBookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            for (Booking booking : sortedBookings) {
                System.out.println("Booking ID: " + booking.getBookingId() +
                                 ", User ID: " + booking.getUserId() +
                                 ", User Balance at Booking: " + booking.getUserBalanceAtBooking() +
                                 ", Room Number: " + booking.getRoomNumber() +
                                 ", Room Type at Booking: " + booking.getRoomTypeAtBooking().getDisplayName() +
                                 ", Room Price/Night at Booking: " + booking.getRoomPricePerNightAtBooking() +
                                 ", Check-in: " + booking.getCheckIn() +
                                 ", Check-out: " + booking.getCheckOut() +
                                 ", Nights: " + booking.getNumberOfNights() +
                                 ", Total Price: " + booking.getTotalPrice());
            }
        }
        System.out.println();
    }

    /**
     * Sets or creates a user with the specified balance.
     */
    public void setUser(int userId, int balance) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        User existingUser = findUserById(userId);
        if (existingUser != null) {
            // Update existing user
            existingUser.setBalance(balance);
        } else {
            // Create new user
            users.add(new User(userId, balance));
        }
    }

    /**
     * Prints all users from latest to oldest.
     */
    public void printAllUsers() {
        System.out.println("=== ALL USERS (Latest to Oldest) ===");
        List<User> sortedUsers = users.stream()
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        if (sortedUsers.isEmpty()) {
            System.out.println("No users available.");
        } else {
            for (User user : sortedUsers) {
                System.out.println("User ID: " + user.getUserId() + 
                                 ", Balance: " + user.getBalance());
            }
        }
        System.out.println();
    }

    // Helper methods

    private Room findRoomByNumber(int roomNumber) {
        return rooms.stream()
            .filter(r -> r.getRoomNumber() == roomNumber)
            .findFirst()
            .orElse(null);
    }

    private User findUserById(int userId) {
        return users.stream()
            .filter(u -> u.getUserId() == userId)
            .findFirst()
            .orElse(null);
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
            .filter(b -> b.getRoomNumber() == roomNumber)
            .noneMatch(b -> b.overlaps(checkIn, checkOut));
    }

    private LocalDate convertToLocalDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    // Getters for testing purposes
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    /**
     * Clears all data (for testing purposes).
     */
    public void clearAll() {
        rooms.clear();
        users.clear();
        bookings.clear();
    }
}
