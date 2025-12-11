package org.skypay;

import org.skypay.exception.*;
import org.skypay.model.RoomType;
import org.skypay.service.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Creates a Date object with only year, month, and day.
     */
    private static Date createDate(int year, int month, int day) {
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

    public static void main(String[] args) {
        System.out.println("Hotel Reservation System - Technical Test");
        System.out.println("==========================================\n");

        Service service = new Service();

        try {
            runTestCase(service);
            System.out.println("\nTest execution completed.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runTestCase(Service service) {
        System.out.println("Creating 3 rooms:");
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        System.out.println("Room 1 created: Type=STANDARD_SUITE, Price=1000/night");
        
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        System.out.println("Room 2 created: Type=JUNIOR_SUITE, Price=2000/night");
        
        service.setRoom(3, RoomType.MASTER_SUITE, 3000);
        System.out.println("Room 3 created: Type=MASTER_SUITE, Price=3000/night");

        System.out.println("\nCreating 2 users:");
        service.setUser(1, 5000);
        System.out.println("User 1 created: Balance=5000");
        
        service.setUser(2, 10000);
        System.out.println("User 2 created: Balance=10000");

        Date june30 = createDate(2026, 6, 30);
        Date july7 = createDate(2026, 7, 7);
        Date july8 = createDate(2026, 7, 8);
        Date july9 = createDate(2026, 7, 9);

        System.out.println("\nTest Scenario 1: User 1 tries booking Room 2 from " + 
                         DATE_FORMAT.format(june30) + " to " + DATE_FORMAT.format(july7) + " (7 nights)");
        try {
            service.bookRoom(1, 2, june30, july7);
            System.out.println("Booking successful");
        } catch (InsufficientBalanceException e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }

        System.out.println("\nTest Scenario 2: User 1 tries booking Room 2 from " + 
                         DATE_FORMAT.format(july7) + " to " + DATE_FORMAT.format(june30));
        try {
            service.bookRoom(1, 2, july7, june30);
            System.out.println("Booking successful");
        } catch (InvalidInputException e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }

        System.out.println("\nTest Scenario 3: User 1 tries booking Room 1 from " + 
                         DATE_FORMAT.format(july7) + " to " + DATE_FORMAT.format(july8) + " (1 night)");
        try {
            service.bookRoom(1, 1, july7, july8);
            System.out.println("Booking successful");
        } catch (Exception e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }

        System.out.println("\nTest Scenario 4: User 2 tries booking Room 1 from " + 
                         DATE_FORMAT.format(july7) + " to " + DATE_FORMAT.format(july9) + " (2 nights)");
        try {
            service.bookRoom(2, 1, july7, july9);
            System.out.println("Booking successful");
        } catch (RoomNotAvailableException e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }

        System.out.println("\nTest Scenario 5: User 2 tries booking Room 3 from " + 
                         DATE_FORMAT.format(july7) + " to " + DATE_FORMAT.format(july8) + " (1 night)");
        try {
            service.bookRoom(2, 3, july7, july8);
            System.out.println("Booking successful");
        } catch (Exception e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }

        System.out.println("\nExecuting setRoom(1, MASTER_SUITE, 10000)");
        service.setRoom(1, RoomType.MASTER_SUITE, 10000);
        System.out.println("Room 1 updated to: Type=MASTER_SUITE, Price=10000/night");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("printAll() output:");
        System.out.println("=".repeat(60));
        service.printAll();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("printAllUsers() output:");
        System.out.println("=".repeat(60));
        service.printAllUsers();
    }
}