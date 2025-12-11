# Hotel Reservation System

## Overview

This is a simplified hotel booking system that manages rooms, users, and reservations. The system validates bookings based on user balance and room availability, while ensuring data integrity through immutable snapshots.

## Architecture

**Entities:**
- `User` - Stores user ID and balance (mutable for transactions)
- `Room` - Stores room number, type, and price (immutable once created)
- `Booking` - Stores booking details with snapshots of room and user state at booking time
- `RoomSnapshot` / `UserSnapshot` - Immutable copies of entity state

**Service Layer:**
- `Service` - Handles all business logic (room/user management, booking validation, printing)

**Exception Handling:**
- `InsufficientBalanceException` - User cannot afford the booking
- `RoomNotAvailableException` - Room already booked for requested dates
- `RoomNotFoundException` / `UserNotFoundException` - Entity not found
- `InvalidInputException` - Invalid input parameters

## Technical Implementation

### 1. Room and User Management

**setRoom(roomNumber, roomType, pricePerNight)**
- Creates new room if it doesn't exist
- Updates existing room if roomNumber matches
- Replaces old room instance entirely (no mutation)

**setUser(userId, balance)**
- Creates new user if it doesn't exist
- Updates balance if user exists

### 2. Booking Logic

**bookRoom(userId, roomNumber, Date checkIn, Date checkOut)**

The method accepts `java.util.Date` parameters as required by the spec, and converts them to `LocalDate` internally (considering only year, month, day).

Validation sequence:
1. Verify user exists
2. Verify room exists
3. Calculate total cost (nights × price)
4. Check user balance >= total cost
5. Check room availability (no date overlaps)
6. Deduct balance and create booking

Date overlap detection:
```java
newCheckIn.isBefore(existingCheckOut) && newCheckOut.isAfter(existingCheckIn)
```

### 3. Immutability Constraint

The requirement states: "setRoom() should not impact previously created bookings."

**Implementation:**  
When a booking is created, we snapshot the current room and user state. Even if the room is later modified via setRoom(), the booking retains the original data.

```java
public Booking(Room room, User user, ...) {
    this.roomSnapshot = new RoomSnapshot(room);  // Copy, not reference
    this.userSnapshot = new UserSnapshot(user);
}
```

**Result:**  
If Room 1 is booked at 1000/night and later updated to 10000/night, the booking still shows 1000/night.

### 4. Printing Order

Both printAll() and printAllUsers() iterate backwards through ArrayLists to print from latest to oldest:
```java
for (int i = list.size() - 1; i >= 0; i--)
```

## Running the Tests

```bash
mvn clean compile exec:java -Dexec.mainClass="org.skypay.Main"
```

Expected output includes:
- 2 successful bookings (User 1 -> Room 1, User 2 -> Room 3)
- 3 rejected bookings (insufficient balance, invalid dates, room conflict)
- Final state showing Room 1 changed to MASTER_SUITE at 10000/night
- User 1's booking still showing STANDARD_SUITE at 1000/night

## Unit Tests

```bash
mvn test
```

23 automated tests covering:
- Entity creation and updates
- Booking validations (balance, availability, dates)
- Exception handling
- Immutability verification
- Print order verification

## Design Questions

### 1. Is a single Service class recommended?

**For this project:** Yes, it's acceptable given the limited scope.

**For production systems:** No, you'd typically separate concerns:
- `RoomService` - Room CRUD operations
- `UserService` - User management
- `BookingService` - Booking orchestration and validation
- `ValidationService` - Centralized input validation

**Reasoning:**  
Single Responsibility Principle states each class should have one reason to change. As features grow (payments, notifications, analytics), a monolithic Service becomes unmaintainable. Splitting by domain allows independent evolution, easier testing, and clearer boundaries.

However, premature abstraction has costs too. For a 5-function prototype, one Service class is pragmatic.

### 2. Alternative approaches to setRoom() immutability?

**Current approach:** Snapshot pattern - copy room data at booking time.

**Alternative:** Temporal/versioned entities - maintain room history with validity periods.

```java
class RoomVersion {
    int roomNumber;
    RoomType type;
    int price;
    LocalDate validFrom;
    LocalDate validTo;
}

// Room history: 
// v1: STANDARD, 1000, 2026-01-01 to 2026-07-05
// v2: MASTER, 10000, 2026-07-06 to infinity

// Booking stores: roomNumber + bookingDate
// On print: lookup which version was valid at bookingDate
```



**Recommendation:** Snapshots.

For a booking system, you need the booking data to be self-contained and immutable. Snapshots achieve this with minimal complexity. Event sourcing/versioning is overkill unless you need:
- Regulatory audit trails
- Time-travel queries ("what was Room 1's price on date X?")
- Rollback capabilities

The snapshot approach trades storage (duplicated room data per booking) for simplicity and query performance. Given that bookings are append-only and room changes are infrequent, this is the right tradeoff.



## Requirements Checklist

- [x] User can book if balance sufficient and room available
- [x] Balance deducted on successful booking
- [x] setRoom() doesn't affect existing bookings
- [x] setRoom() creates or updates room
- [x] setUser() creates or updates user
- [x] printAll() shows rooms and bookings (latest to oldest)
- [x] Bookings contain room/user data at booking time
- [x] printAllUsers() shows users (latest to oldest)
- [x] ArrayLists used (no repositories)
- [x] Dates handle year/month/day only
- [x] Exception handling for invalid inputs
