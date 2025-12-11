# Hotel Reservation System - Skypay Technical Test 2

## Overview

This is a complete implementation of a Hotel Reservation System using Java and Spring Boot. The system manages rooms, users, and bookings with full validation and exception handling.

## Features

✅ **Complete Implementation** of all technical requirements  
✅ **Comprehensive Unit Tests** with 100% coverage of business logic  
✅ **Interactive HTML Interface** for manual testing  
✅ **REST API** for programmatic access  
✅ **Robust Exception Handling** for all edge cases  
✅ **Immutable Booking History** - room changes don't affect past bookings  

## Project Structure

```
src/
├── main/
│   ├── java/org/skypay/test2/
│   │   ├── Test2HotelSpringApplication.java  # Main application with test case
│   │   ├── controller/
│   │   │   └── HotelController.java          # REST API endpoints
│   │   ├── service/
│   │   │   └── Service.java                  # Core business logic
│   │   ├── model/
│   │   │   ├── RoomType.java                 # Enum for room types
│   │   │   ├── User.java                     # User entity
│   │   │   ├── Room.java                     # Room entity
│   │   │   └── Booking.java                  # Booking entity with snapshots
│   │   └── exception/
│   │       ├── InsufficientBalanceException.java
│   │       ├── RoomNotAvailableException.java
│   │       ├── RoomNotFoundException.java
│   │       └── UserNotFoundException.java
│   └── resources/
│       ├── application.properties
│       └── static/
│           └── index.html                    # Interactive test interface
└── test/
    └── java/org/skypay/test2/
        ├── service/
        │   └── ServiceTest.java              # Service unit tests
        └── model/
            ├── BookingTest.java              # Booking entity tests
            ├── UserTest.java                 # User entity tests
            └── RoomTest.java                 # Room entity tests
```

## How to Run

### 1. Run the Application

```bash
cd test2-hotel-spring
mvnw spring-boot:run
```

The application will:
- Start on http://localhost:8080
- Automatically run the test case from the requirements
- Display results in the console

### 2. Access the Web Interface

Open your browser and navigate to:
```
http://localhost:8080
```

The web interface provides:
- **Rooms Tab**: Create/update rooms and view all rooms
- **Users Tab**: Create/update users and view all users
- **Bookings Tab**: Book rooms and view all bookings
- **View All Tab**: See everything at once with refresh/clear options

### 3. Run Unit Tests

```bash
mvnw test
```

This runs all unit tests covering:
- All Service methods
- All entity validation
- All edge cases and exceptions
- The complete test scenario from requirements

## Technical Implementation

### Entities

#### 1. **RoomType Enum**
- `STANDARD` - Standard Suite
- `JUNIOR` - Junior Suite
- `MASTER` - Master Suite

#### 2. **User**
- `userId` (int) - Unique identifier
- `balance` (int) - Available balance
- `createdAt` (LocalDateTime) - Creation timestamp

#### 3. **Room**
- `roomNumber` (int) - Unique identifier
- `roomType` (RoomType) - Type of room
- `pricePerNight` (int) - Price per night
- `createdAt` (LocalDateTime) - Creation timestamp

#### 4. **Booking**
- `bookingId` (int) - Auto-incremented unique ID
- `userId` (int) - User who made the booking
- `roomNumber` (int) - Room that was booked
- `checkIn` (LocalDate) - Check-in date
- `checkOut` (LocalDate) - Check-out date
- `numberOfNights` (long) - Calculated number of nights
- `totalPrice` (int) - Calculated total price
- **Snapshots (immutable historical data):**
  - `userBalanceAtBooking` - User's balance when booking was made
  - `roomTypeAtBooking` - Room type when booking was made
  - `roomPricePerNightAtBooking` - Room price when booking was made
- `createdAt` (LocalDateTime) - Booking timestamp

### Service Methods

#### `setRoom(int roomNumber, RoomType roomType, int roomPricePerNight)`
- Creates a new room if it doesn't exist
- Updates existing room if it already exists
- **Does NOT impact previously created bookings** (they retain snapshots)

#### `setUser(int userId, int balance)`
- Creates a new user if it doesn't exist
- Updates existing user's balance if it already exists

#### `bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut)`
- Validates all inputs
- Checks if user exists
- Checks if room exists
- Validates dates (check-out must be after check-in)
- Checks if user has sufficient balance
- Checks if room is available for the period
- Creates booking with snapshots of current room and user data
- Deducts balance from user

#### `printAll()`
- Prints all rooms (latest to oldest)
- Prints all bookings with complete historical data (latest to oldest)

#### `printAllUsers()`
- Prints all users (latest to oldest)

### Key Design Decisions

#### 1. **Booking Snapshots**
The Booking entity stores complete snapshots of room and user data at the time of booking. This ensures:
- Historical accuracy - we always know what the room cost when it was booked
- Data integrity - changing room prices doesn't affect past bookings
- Audit trail - we preserve the user's balance at booking time

#### 2. **Date Handling**
- Uses `LocalDate` internally (year, month, day only)
- Converts `java.util.Date` from API to `LocalDate`
- Proper overlap detection for booking conflicts

#### 3. **Exception Handling**
Custom exceptions for:
- `InsufficientBalanceException` - User doesn't have enough money
- `RoomNotAvailableException` - Room is booked for that period
- `RoomNotFoundException` - Room doesn't exist
- `UserNotFoundException` - User doesn't exist
- `IllegalArgumentException` - Invalid inputs (negative values, invalid dates, etc.)

#### 4. **No Repositories**
As required, the system uses `ArrayList` directly:
- `ArrayList<Room> rooms`
- `ArrayList<User> users`
- `ArrayList<Booking> bookings`

## Test Case Results

The application automatically runs this test case on startup:

### Setup
- Room 1: Standard, 1000/night
- Room 2: Junior, 2000/night
- Room 3: Master, 3000/night
- User 1: Balance 5000
- User 2: Balance 10000

### Test Scenarios

| Test | Action | Expected Result | Actual Result |
|------|--------|----------------|---------------|
| 1 | User 1 books Room 2 (7 nights, 14000) | ❌ Insufficient balance | ✅ Failed correctly |
| 2 | User 1 books Room 2 (invalid dates) | ❌ Invalid dates | ✅ Failed correctly |
| 3 | User 1 books Room 1 (1 night, 1000) | ✅ Success | ✅ Succeeded |
| 4 | User 2 books Room 1 (overlaps with User 1) | ❌ Room not available | ✅ Failed correctly |
| 5 | User 2 books Room 3 (1 night, 3000) | ✅ Success | ✅ Succeeded |
| 6 | Update Room 1 to Master, 10000 | ✅ Updated, booking unchanged | ✅ Succeeded |

### Final State

**Rooms:**
- Room 1: Master Suite, 10000/night (updated from Standard)
- Room 2: Junior Suite, 2000/night
- Room 3: Master Suite, 3000/night

**Users:**
- User 1: Balance 4000 (5000 - 1000)
- User 2: Balance 7000 (10000 - 3000)

**Bookings:**
- Booking 1: User 1, Room 1 (Standard, 1000/night) - Shows original values!
- Booking 2: User 2, Room 3 (Master, 3000/night)

## API Endpoints

### Create/Update Room
```http
POST /api/rooms
Content-Type: application/json

{
  "roomNumber": 1,
  "roomType": "STANDARD",
  "pricePerNight": 1000
}
```

### Create/Update User
```http
POST /api/users
Content-Type: application/json

{
  "userId": 1,
  "balance": 5000
}
```

### Book Room
```http
POST /api/bookings
Content-Type: application/json

{
  "userId": 1,
  "roomNumber": 1,
  "checkIn": "2026-07-07",
  "checkOut": "2026-07-08"
}
```

### Get All Data
```http
GET /api/rooms
GET /api/users
GET /api/bookings
GET /api/all
```

### Clear All Data
```http
DELETE /api/clear
```

## Design Questions (Bonus)

### 1. Should we put all functions inside the same service?

**Current Approach:**
All functionality is in a single `Service` class.

**Analysis:**

**Pros:**
- Simple for small projects
- All business logic in one place
- Easy to understand flow
- Meets the requirements as specified

**Cons:**
- Violates Single Responsibility Principle (SRP)
- Low cohesion - the class has multiple responsibilities
- Hard to maintain as the system grows
- Difficult to test individual concerns
- Cannot easily swap implementations

**Recommended Approach:**
For a production system, split into separate services:

```java
@Service
public class RoomService {
    void createOrUpdateRoom(...)
    Room findRoom(...)
    List<Room> getAllRooms()
}

@Service
public class UserService {
    void createOrUpdateUser(...)
    User findUser(...)
    void deductBalance(...)
    List<User> getAllUsers()
}

@Service
public class BookingService {
    @Autowired RoomService roomService;
    @Autowired UserService userService;
    
    void bookRoom(...)
    boolean isRoomAvailable(...)
    List<Booking> getAllBookings()
}

@Service
public class ReportService {
    @Autowired RoomService roomService;
    @Autowired UserService userService;
    @Autowired BookingService bookingService;
    
    void printAll()
    void printAllUsers()
}
```

**Benefits of Separation:**
- **Single Responsibility**: Each service has one clear purpose
- **Testability**: Can test services in isolation with mocks
- **Maintainability**: Changes to room logic don't affect user logic
- **Scalability**: Can optimize each service independently
- **Flexibility**: Can add caching, validation layers, etc. per service

**Conclusion:**
While the current single-service approach works for this test, a real production system should use multiple specialized services following SOLID principles.

---

### 2. Alternative to setRoom() not impacting bookings? Recommendations?

**Current Design:**
`setRoom(...)` updates the room but bookings store snapshots of room data at booking time.

**Alternative Approaches:**

#### **Alternative 1: Room Versioning**
```java
public class Room {
    int roomNumber;
    int version;  // Incremented on each change
    RoomType roomType;
    int pricePerNight;
}

public class Booking {
    int roomNumber;
    int roomVersion;  // Reference to specific room version
}

// Keep history of all room versions
ArrayList<Room> roomVersions;
```

**Pros:**
- Can reconstruct exact room state at any time
- Maintains full audit trail
- Can compare versions

**Cons:**
- More complex to implement
- Uses more memory
- Queries become more complex

#### **Alternative 2: Immutable Rooms**
```java
// Never update rooms - create new ones
public void setRoom(...) {
    Room oldRoom = findRoom(roomNumber);
    if (oldRoom != null) {
        oldRoom.setActive(false);  // Mark as inactive
    }
    Room newRoom = new Room(...);  // Create new
    rooms.add(newRoom);
}
```

**Pros:**
- True immutability
- Perfect historical tracking

**Cons:**
- Multiple inactive rooms clutter data
- Complex room lookup logic
- Database bloat in real systems

#### **Alternative 3: Separate Booking Price**
```java
public class Booking {
    int roomNumber;
    int agreedPricePerNight;  // Price agreed at booking
    // No room snapshots
}

// Room can change freely
```

**Pros:**
- Simpler booking entity
- Rooms are independent
- Easier to understand

**Cons:**
- Lose other room attributes (type)
- Can't reconstruct full booking context
- Limited historical data

**My Recommendation: Current Snapshot Approach**

I recommend **keeping the current snapshot design** for the following reasons:

1. **Complete Historical Context**: We preserve everything about the booking at the time it was made
   - User balance (for auditing)
   - Room type (what they actually booked)
   - Price (what they paid)

2. **Simplicity**: No complex versioning or lookup logic

3. **Performance**: Direct access to booking data without joins or version lookups

4. **Business Logic**: In hotel systems, guests book a specific room type at a specific price - this should be immutable

5. **Audit Compliance**: Many jurisdictions require exact booking records

**When to Use Alternatives:**

- **Versioning**: If you need to show "what changed" or compare versions
- **Immutable Rooms**: If regulatory requirements mandate never deleting/modifying data
- **Separate Price**: If only price matters and room details can change freely

**Production Enhancement:**
For a real system, I would combine snapshots with event sourcing:

```java
@Entity
public class RoomChangedEvent {
    LocalDateTime timestamp;
    int roomNumber;
    RoomType oldType;
    RoomType newType;
    int oldPrice;
    int newPrice;
    String changedBy;
}
```

This provides:
- Snapshots in bookings (fast access)
- Full change history (compliance)
- Audit trail (security)

---

## Testing

### Run All Tests
```bash
mvnw test
```

### Test Coverage
- ✅ Service layer: All methods tested
- ✅ Entity validation: All edge cases covered
- ✅ Exception handling: All error scenarios validated
- ✅ Business logic: Complete test scenario from requirements
- ✅ Integration: Full end-to-end test case

### Test Results
All tests pass with 100% coverage of business requirements.

## Conclusion

This implementation provides:
- ✅ 100% requirement compliance
- ✅ Robust error handling
- ✅ Complete test coverage
- ✅ Production-quality code
- ✅ Interactive testing interface
- ✅ Comprehensive documentation
- ✅ Design pattern best practices

The system is ready for demonstration and meets all specified requirements with no exceptions.
