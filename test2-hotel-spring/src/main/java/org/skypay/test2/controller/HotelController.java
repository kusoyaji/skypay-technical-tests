package org.skypay.test2.controller;

import org.skypay.test2.model.Booking;
import org.skypay.test2.model.Room;
import org.skypay.test2.model.RoomType;
import org.skypay.test2.model.User;
import org.skypay.test2.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for the Hotel Reservation System web interface.
 */
@RestController
@RequestMapping("/api")
public class HotelController {

    @Autowired
    private Service service;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Create or update a room.
     */
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, String>> setRoom(@RequestBody Map<String, Object> request) {
        try {
            int roomNumber = Integer.parseInt(request.get("roomNumber").toString());
            RoomType roomType = RoomType.valueOf(request.get("roomType").toString().toUpperCase());
            int pricePerNight = Integer.parseInt(request.get("pricePerNight").toString());

            service.setRoom(roomNumber, roomType, pricePerNight);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Room " + roomNumber + " created/updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Create or update a user.
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> setUser(@RequestBody Map<String, Object> request) {
        try {
            int userId = Integer.parseInt(request.get("userId").toString());
            int balance = Integer.parseInt(request.get("balance").toString());

            service.setUser(userId, balance);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User " + userId + " created/updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Book a room.
     */
    @PostMapping("/bookings")
    public ResponseEntity<Map<String, String>> bookRoom(@RequestBody Map<String, Object> request) {
        try {
            int userId = Integer.parseInt(request.get("userId").toString());
            int roomNumber = Integer.parseInt(request.get("roomNumber").toString());
            Date checkIn = dateFormat.parse(request.get("checkIn").toString());
            Date checkOut = dateFormat.parse(request.get("checkOut").toString());

            service.bookRoom(userId, roomNumber, checkIn, checkOut);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Booking successful");
            return ResponseEntity.ok(response);
        } catch (ParseException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid date format. Use yyyy-MM-dd");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all rooms.
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<Map<String, Object>>> getAllRooms() {
        List<Map<String, Object>> roomList = service.getRooms().stream()
            .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
            .map(room -> {
                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("roomNumber", room.getRoomNumber());
                roomMap.put("roomType", room.getRoomType().name());
                roomMap.put("roomTypeDisplay", room.getRoomType().getDisplayName());
                roomMap.put("pricePerNight", room.getPricePerNight());
                roomMap.put("createdAt", room.getCreatedAt().toString());
                return roomMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(roomList);
    }

    /**
     * Get all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> userList = service.getUsers().stream()
            .sorted(Comparator.comparing(User::getCreatedAt).reversed())
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("balance", user.getBalance());
                userMap.put("createdAt", user.getCreatedAt().toString());
                return userMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(userList);
    }

    /**
     * Get all bookings.
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<Map<String, Object>>> getAllBookings() {
        List<Map<String, Object>> bookingList = service.getBookings().stream()
            .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
            .map(booking -> {
                Map<String, Object> bookingMap = new HashMap<>();
                bookingMap.put("bookingId", booking.getBookingId());
                bookingMap.put("userId", booking.getUserId());
                bookingMap.put("roomNumber", booking.getRoomNumber());
                bookingMap.put("checkIn", booking.getCheckIn().toString());
                bookingMap.put("checkOut", booking.getCheckOut().toString());
                bookingMap.put("numberOfNights", booking.getNumberOfNights());
                bookingMap.put("totalPrice", booking.getTotalPrice());
                bookingMap.put("userBalanceAtBooking", booking.getUserBalanceAtBooking());
                bookingMap.put("roomTypeAtBooking", booking.getRoomTypeAtBooking().name());
                bookingMap.put("roomTypeAtBookingDisplay", booking.getRoomTypeAtBooking().getDisplayName());
                bookingMap.put("roomPricePerNightAtBooking", booking.getRoomPricePerNightAtBooking());
                bookingMap.put("createdAt", booking.getCreatedAt().toString());
                return bookingMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(bookingList);
    }

    /**
     * Get all data (rooms, users, bookings).
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = new HashMap<>();
        response.put("rooms", getAllRooms().getBody());
        response.put("users", getAllUsers().getBody());
        response.put("bookings", getAllBookings().getBody());
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all data.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAll() {
        service.clearAll();
        Map<String, String> response = new HashMap<>();
        response.put("message", "All data cleared successfully");
        return ResponseEntity.ok(response);
    }
}
