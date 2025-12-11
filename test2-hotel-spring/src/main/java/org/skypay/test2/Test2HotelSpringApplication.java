package org.skypay.test2;

import org.skypay.test2.model.RoomType;
import org.skypay.test2.service.Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class Test2HotelSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(Test2HotelSpringApplication.class, args);
    }

    @Bean
    CommandLineRunner runTestCase(Service service) {
        return args -> {
            System.out.println("\n========================================");
            System.out.println("HOTEL RESERVATION SYSTEM - TEST CASE");
            System.out.println("========================================\n");

            // Create 3 rooms
            System.out.println(">>> Creating 3 rooms...");
            service.setRoom(1, RoomType.STANDARD, 1000);
            service.setRoom(2, RoomType.JUNIOR, 2000);
            service.setRoom(3, RoomType.MASTER, 3000);
            System.out.println("✓ Rooms created successfully\n");

            // Create 2 users
            System.out.println(">>> Creating 2 users...");
            service.setUser(1, 5000);
            service.setUser(2, 10000);
            System.out.println("✓ Users created successfully\n");

            // Test Case 1: User 1 tries booking Room 2 from 30/06/2026 to 07/07/2026 (7 nights)
            // Required: 7 * 2000 = 14000, Available: 5000 - Should FAIL
            System.out.println(">>> Test 1: User 1 books Room 2 from 30/06/2026 to 07/07/2026 (7 nights, cost: 14000)");
            try {
                service.bookRoom(1, 2, createDate(2026, 6, 30), createDate(2026, 7, 7));
                System.out.println("✓ Booking successful");
            } catch (Exception e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
            System.out.println();

            // Test Case 2: User 1 tries booking Room 2 from 07/07/2026 to 30/06/2026 (invalid dates)
            // Should FAIL - check-out before check-in
            System.out.println(">>> Test 2: User 1 books Room 2 from 07/07/2026 to 30/06/2026 (invalid dates)");
            try {
                service.bookRoom(1, 2, createDate(2026, 7, 7), createDate(2026, 6, 30));
                System.out.println("✓ Booking successful");
            } catch (Exception e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
            System.out.println();

            // Test Case 3: User 1 tries booking Room 1 from 07/07/2026 to 08/07/2026 (1 night)
            // Required: 1 * 1000 = 1000, Available: 5000 - Should SUCCEED
            System.out.println(">>> Test 3: User 1 books Room 1 from 07/07/2026 to 08/07/2026 (1 night, cost: 1000)");
            try {
                service.bookRoom(1, 1, createDate(2026, 7, 7), createDate(2026, 7, 8));
                System.out.println("✓ Booking successful");
            } catch (Exception e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
            System.out.println();

            // Test Case 4: User 2 tries booking Room 1 from 07/07/2026 to 09/07/2026 (2 nights)
            // Room 1 is already booked by User 1 for 07/07 - Should FAIL
            System.out.println(">>> Test 4: User 2 books Room 1 from 07/07/2026 to 09/07/2026 (2 nights, overlaps with User 1)");
            try {
                service.bookRoom(2, 1, createDate(2026, 7, 7), createDate(2026, 7, 9));
                System.out.println("✓ Booking successful");
            } catch (Exception e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
            System.out.println();

            // Test Case 5: User 2 tries booking Room 3 from 07/07/2026 to 08/07/2026 (1 night)
            // Required: 1 * 3000 = 3000, Available: 10000 - Should SUCCEED
            System.out.println(">>> Test 5: User 2 books Room 3 from 07/07/2026 to 08/07/2026 (1 night, cost: 3000)");
            try {
                service.bookRoom(2, 3, createDate(2026, 7, 7), createDate(2026, 7, 8));
                System.out.println("✓ Booking successful");
            } catch (Exception e) {
                System.out.println("✗ Booking failed: " + e.getMessage());
            }
            System.out.println();

            // Test Case 6: setRoom(1, MASTER, 10000)
            // This should update Room 1 but NOT impact the existing booking
            System.out.println(">>> Test 6: Updating Room 1 to Master Suite with price 10000");
            service.setRoom(1, RoomType.MASTER, 10000);
            System.out.println("✓ Room updated successfully");
            System.out.println();

            // Print final results
            System.out.println("\n========================================");
            System.out.println("FINAL RESULTS");
            System.out.println("========================================\n");
            
            service.printAll();
            service.printAllUsers();

            System.out.println("========================================");
            System.out.println("TEST CASE COMPLETED");
            System.out.println("========================================\n");
        };
    }

    /**
     * Helper method to create Date objects
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
}
