package org.skypay.service;

import org.skypay.exception.*;
import org.skypay.model.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class Service {
    public final ArrayList<Room> rooms;
    public final ArrayList<User> users;
    public final ArrayList<Booking> bookings;
    private int nextBookingId;

    public Service() {
        this.rooms = new ArrayList<>();
        this.users = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.nextBookingId = 1;
    }

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        validateRoomInput(roomNumber, roomType, roomPricePerNight);

        Optional<Room> existingRoom = findRoomByNumber(roomNumber);
        
        if (existingRoom.isPresent()) {
            rooms.remove(existingRoom.get());
        }
        
        rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
    }

    public void setUser(int userId, int balance) {
        validateUserInput(userId, balance);

        Optional<User> existingUser = findUserById(userId);
        
        if (existingUser.isPresent()) {
            existingUser.get().setBalance(balance);
        } else {
            users.add(new User(userId, balance));
        }
    }

    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        // Convert Date to LocalDate (considering only year, month, day as per requirements)
        LocalDate checkInDate = checkIn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOutDate = checkOut.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        validateBookingInput(userId, roomNumber, checkInDate, checkOutDate);

        User user = findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Room room = findRoomByNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room with number " + roomNumber + " not found"));

        int numberOfNights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        int totalCost = numberOfNights * room.getPricePerNight();

        if (user.getBalance() < totalCost) {
            throw new InsufficientBalanceException(
                    "User " + userId + " has insufficient balance. Required: " + totalCost + ", Available: " + user.getBalance());
        }

        if (!isRoomAvailable(roomNumber, checkInDate, checkOutDate)) {
            throw new RoomNotAvailableException(
                    "Room " + roomNumber + " is not available for the period " + checkInDate + " to " + checkOutDate);
        }

        user.setBalance(user.getBalance() - totalCost);
        
        Booking booking = new Booking(nextBookingId++, room, user, checkInDate, checkOutDate);
        bookings.add(booking);
    }

    public void printAll() {
        System.out.println("All Rooms (latest to oldest):");
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            for (int i = rooms.size() - 1; i >= 0; i--) {
                System.out.println(rooms.get(i));
            }
        }

        System.out.println("\nAll Bookings (latest to oldest):");
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            for (int i = bookings.size() - 1; i >= 0; i--) {
                System.out.println(bookings.get(i));
            }
        }
    }

    public void printAllUsers() {
        System.out.println("All Users (latest to oldest):");
        if (users.isEmpty()) {
            System.out.println("No users available.");
        } else {
            for (int i = users.size() - 1; i >= 0; i--) {
                System.out.println(users.get(i));
            }
        }
    }

    private Optional<Room> findRoomByNumber(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst();
    }

    private Optional<User> findUserById(int userId) {
        return users.stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst();
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
                .filter(booking -> booking.getRoomSnapshot().getRoomNumber() == roomNumber)
                .noneMatch(booking -> hasOverlap(booking.getCheckIn(), booking.getCheckOut(), checkIn, checkOut));
    }

    private boolean hasOverlap(LocalDate existingCheckIn, LocalDate existingCheckOut, 
                               LocalDate newCheckIn, LocalDate newCheckOut) {
        return newCheckIn.isBefore(existingCheckOut) && newCheckOut.isAfter(existingCheckIn);
    }

    private void validateRoomInput(int roomNumber, RoomType roomType, int roomPricePerNight) {
        if (roomNumber <= 0) {
            throw new InvalidInputException("Room number must be positive");
        }
        if (roomType == null) {
            throw new InvalidInputException("Room type cannot be null");
        }
        if (roomPricePerNight <= 0) {
            throw new InvalidInputException("Room price per night must be positive");
        }
    }

    private void validateUserInput(int userId, int balance) {
        if (userId <= 0) {
            throw new InvalidInputException("User ID must be positive");
        }
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative");
        }
    }

    private void validateBookingInput(int userId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (userId <= 0) {
            throw new InvalidInputException("User ID must be positive");
        }
        if (roomNumber <= 0) {
            throw new InvalidInputException("Room number must be positive");
        }
        if (checkIn == null) {
            throw new InvalidInputException("Check-in date cannot be null");
        }
        if (checkOut == null) {
            throw new InvalidInputException("Check-out date cannot be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidInputException("Check-out date must be after check-in date");
        }
    }
}
