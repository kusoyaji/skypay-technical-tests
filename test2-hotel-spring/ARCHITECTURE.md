# Project Architecture

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Hotel Reservation System                  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────┐      ┌─────────────────────┐
│   Web Browser       │      │   Console Output    │
│   (HTML/JS)         │      │   (Test Results)    │
└──────────┬──────────┘      └──────────┬──────────┘
           │                            │
           │ HTTP/REST                  │
           │                            │
┌──────────▼─────────────────────────────▼──────────┐
│          Spring Boot Application                  │
│  ┌─────────────────────────────────────────────┐  │
│  │         HotelController (REST API)          │  │
│  │  POST /api/rooms     GET /api/rooms        │  │
│  │  POST /api/users     GET /api/users        │  │
│  │  POST /api/bookings  GET /api/bookings     │  │
│  └──────────────────┬──────────────────────────┘  │
│                     │                              │
│  ┌──────────────────▼──────────────────────────┐  │
│  │            Service Layer                    │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │         Service Class                  │ │  │
│  │  │  • setRoom()                          │ │  │
│  │  │  • setUser()                          │ │  │
│  │  │  • bookRoom()                         │ │  │
│  │  │  • printAll()                         │ │  │
│  │  │  • printAllUsers()                    │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  └──────────────────┬──────────────────────────┘  │
│                     │                              │
│  ┌──────────────────▼──────────────────────────┐  │
│  │            Data Layer                       │  │
│  │  ┌────────────────────────────────────────┐ │  │
│  │  │     ArrayList<Room>                    │ │  │
│  │  │     ArrayList<User>                    │ │  │
│  │  │     ArrayList<Booking>                 │ │  │
│  │  └────────────────────────────────────────┘ │  │
│  └─────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────┘
```

## Entity Relationships

```
┌─────────────────┐
│      User       │
│─────────────────│
│ • userId (PK)   │
│ • balance       │
│ • createdAt     │
└────────┬────────┘
         │
         │ 1:N (creates)
         │
         ▼
┌─────────────────┐         ┌─────────────────┐
│    Booking      │ N:1     │      Room       │
│─────────────────│◄────────│─────────────────│
│ • bookingId(PK) │  (for)  │ • roomNumber(PK)│
│ • userId        │         │ • roomType      │
│ • roomNumber    │         │ • pricePerNight │
│ • checkIn       │         │ • createdAt     │
│ • checkOut      │         └─────────────────┘
│ • nights        │
│ • totalPrice    │
│─────────────────│
│ SNAPSHOTS:      │
│ • userBalanceAt │
│ • roomTypeAt    │
│ • roomPriceAt   │
│ • createdAt     │
└─────────────────┘
```

## Data Flow: Creating a Booking

```
1. Request
   ┌─────────────────────────────────────────┐
   │ POST /api/bookings                      │
   │ {                                       │
   │   userId: 1,                           │
   │   roomNumber: 101,                     │
   │   checkIn: "2026-07-07",              │
   │   checkOut: "2026-07-08"              │
   │ }                                       │
   └──────────────┬──────────────────────────┘
                  │
2. Validation     ▼
   ┌─────────────────────────────────────────┐
   │ Service.bookRoom()                      │
   │ • Validate inputs                       │
   │ • Find user (throws if not found)       │
   │ • Find room (throws if not found)       │
   │ • Validate dates                        │
   │ • Check room availability               │
   │ • Check user balance                    │
   └──────────────┬──────────────────────────┘
                  │
3. Execute        ▼
   ┌─────────────────────────────────────────┐
   │ Create Booking                          │
   │ • Capture room snapshot                 │
   │ • Capture user snapshot                 │
   │ • Calculate price                       │
   │ • Deduct from user balance              │
   │ • Add to bookings list                  │
   └──────────────┬──────────────────────────┘
                  │
4. Response       ▼
   ┌─────────────────────────────────────────┐
   │ { message: "Booking successful" }       │
   └─────────────────────────────────────────┘
```

## Snapshot Pattern Flow

```
Timeline: Room Changes but Booking Remains Unchanged

Time T1: Initial State
┌──────────────┐      ┌──────────────┐
│   Room 1     │      │   User 1     │
│──────────────│      │──────────────│
│ Type: STANDARD│     │ Balance:5000│
│ Price: 1000  │      └──────────────┘
└──────────────┘

Time T2: Booking Created
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   Room 1     │      │   User 1     │      │  Booking 1   │
│──────────────│      │──────────────│      │──────────────│
│ Type: STANDARD│     │ Balance:4000│      │ UserId: 1    │
│ Price: 1000  │      └──────────────┘      │ RoomNum: 1   │
└──────────────┘                            │──────────────│
                                            │ SNAPSHOT:    │
                                            │ UserBal:5000│
                                            │ Type:STANDARD│
                                            │ Price: 1000  │
                                            └──────────────┘

Time T3: Room Updated (setRoom called)
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   Room 1     │      │   User 1     │      │  Booking 1   │
│──────────────│      │──────────────│      │──────────────│
│ Type: MASTER │◄─X   │ Balance:4000│      │ UserId: 1    │
│ Price: 10000 │      └──────────────┘      │ RoomNum: 1   │
└──────────────┘                            │──────────────│
     UPDATED!                               │ SNAPSHOT:    │
                                            │ UserBal:5000│◄─ UNCHANGED!
                                            │ Type:STANDARD│◄─ UNCHANGED!
                                            │ Price: 1000  │◄─ UNCHANGED!
                                            └──────────────┘

Key: ◄─X = No connection (snapshot is independent)
```

## Class Structure

```
┌─────────────────────────────────────────────────────┐
│                    model package                     │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────────┐    ┌──────────────┐               │
│  │   RoomType   │    │     User     │               │
│  │──────────────│    │──────────────│               │
│  │ • STANDARD   │    │ - userId     │               │
│  │ • JUNIOR     │    │ - balance    │               │
│  │ • MASTER     │    │ - createdAt  │               │
│  └──────────────┘    │ + getters    │               │
│                      │ + setBalance │               │
│  ┌──────────────┐    │ + deduct     │               │
│  │     Room     │    └──────────────┘               │
│  │──────────────│                                    │
│  │ - roomNumber │    ┌──────────────┐               │
│  │ - roomType   │    │   Booking    │               │
│  │ - price      │    │──────────────│               │
│  │ - createdAt  │    │ - bookingId  │               │
│  │ + getters    │    │ - userId     │               │
│  │ + setters    │    │ - roomNumber │               │
│  └──────────────┘    │ - checkIn    │               │
│                      │ - checkOut   │               │
│                      │ - nights     │               │
│                      │ - totalPrice │               │
│                      │ - SNAPSHOTS  │               │
│                      │ + overlaps() │               │
│                      └──────────────┘               │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                  exception package                   │
├─────────────────────────────────────────────────────┤
│  • InsufficientBalanceException                      │
│  • RoomNotAvailableException                         │
│  • RoomNotFoundException                             │
│  • UserNotFoundException                             │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                   service package                    │
├─────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────┐    │
│  │              Service                        │    │
│  │─────────────────────────────────────────────│    │
│  │ - ArrayList<Room> rooms                     │    │
│  │ - ArrayList<User> users                     │    │
│  │ - ArrayList<Booking> bookings               │    │
│  │─────────────────────────────────────────────│    │
│  │ + setRoom()                                 │    │
│  │ + setUser()                                 │    │
│  │ + bookRoom()                                │    │
│  │ + printAll()                                │    │
│  │ + printAllUsers()                           │    │
│  │─────────────────────────────────────────────│    │
│  │ - findRoomByNumber()                        │    │
│  │ - findUserById()                            │    │
│  │ - isRoomAvailable()                         │    │
│  │ - convertToLocalDate()                      │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                 controller package                   │
├─────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────┐    │
│  │          HotelController                    │    │
│  │─────────────────────────────────────────────│    │
│  │ @Autowired Service service                  │    │
│  │─────────────────────────────────────────────│    │
│  │ @POST   /api/rooms      → setRoom()        │    │
│  │ @POST   /api/users      → setUser()        │    │
│  │ @POST   /api/bookings   → bookRoom()       │    │
│  │ @GET    /api/rooms      → getAllRooms()    │    │
│  │ @GET    /api/users      → getAllUsers()    │    │
│  │ @GET    /api/bookings   → getAllBookings() │    │
│  │ @GET    /api/all        → getAll()         │    │
│  │ @DELETE /api/clear      → clearAll()       │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

## File Structure

```
test2-hotel-spring/
│
├── src/
│   ├── main/
│   │   ├── java/org/skypay/test2/
│   │   │   ├── Test2HotelSpringApplication.java    ← Main + Test Case
│   │   │   ├── controller/
│   │   │   │   └── HotelController.java            ← REST API
│   │   │   ├── service/
│   │   │   │   └── Service.java                    ← Business Logic
│   │   │   ├── model/
│   │   │   │   ├── RoomType.java                   ← Enum
│   │   │   │   ├── User.java                       ← Entity
│   │   │   │   ├── Room.java                       ← Entity
│   │   │   │   └── Booking.java                    ← Entity
│   │   │   └── exception/
│   │   │       ├── InsufficientBalanceException.java
│   │   │       ├── RoomNotAvailableException.java
│   │   │       ├── RoomNotFoundException.java
│   │   │       └── UserNotFoundException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html                      ← Web Interface
│   │
│   └── test/
│       └── java/org/skypay/test2/
│           ├── service/
│           │   └── ServiceTest.java                ← Service Tests
│           └── model/
│               ├── BookingTest.java                ← Entity Tests
│               ├── UserTest.java
│               └── RoomTest.java
│
├── pom.xml                                         ← Maven Config
├── README.md                                       ← Full Documentation
├── QUICKSTART.md                                   ← Quick Start Guide
├── DESIGN_ANSWERS.md                               ← Design Questions
└── ARCHITECTURE.md                                 ← This File
```

## Test Coverage

```
┌─────────────────────────────────────────────────────┐
│                   Test Structure                     │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ServiceTest (25+ tests)                            │
│  ├── setRoom tests                                  │
│  │   ├── Creates new room                          │
│  │   ├── Updates existing room                     │
│  │   ├── Invalid inputs                            │
│  │   └── Doesn't impact bookings ✓                 │
│  ├── setUser tests                                  │
│  │   ├── Creates new user                          │
│  │   ├── Updates existing user                     │
│  │   └── Invalid inputs                            │
│  ├── bookRoom tests                                 │
│  │   ├── Successful booking                        │
│  │   ├── Insufficient balance                      │
│  │   ├── Room not found                            │
│  │   ├── User not found                            │
│  │   ├── Invalid dates                             │
│  │   ├── Room not available                        │
│  │   ├── Consecutive bookings                      │
│  │   └── Multiple nights                           │
│  ├── printAll tests                                 │
│  ├── printAllUsers tests                            │
│  └── Complete scenario test ✓                      │
│                                                      │
│  BookingTest (12+ tests)                            │
│  ├── Valid creation                                 │
│  ├── Invalid inputs                                 │
│  ├── Overlap detection                              │
│  └── ID increment                                   │
│                                                      │
│  UserTest (10+ tests)                               │
│  ├── Valid creation                                 │
│  ├── Invalid inputs                                 │
│  ├── Balance operations                             │
│  └── Equality                                       │
│                                                      │
│  RoomTest (10+ tests)                               │
│  ├── Valid creation                                 │
│  ├── Invalid inputs                                 │
│  ├── Property updates                               │
│  └── Equality                                       │
│                                                      │
└─────────────────────────────────────────────────────┘

Total: 57+ Unit Tests
Coverage: 100% of business requirements
```

## Deployment

```
Development Environment
┌─────────────────────────────────────────────┐
│  Local Machine                              │
│  ├── mvnw spring-boot:run                  │
│  ├── http://localhost:8080                 │
│  └── Embedded Tomcat Server                │
└─────────────────────────────────────────────┘

Production Environment (Future)
┌─────────────────────────────────────────────┐
│  Cloud Server (AWS/Azure/GCP)              │
│  ├── mvn clean package                     │
│  ├── java -jar target/app.jar              │
│  ├── PostgreSQL Database                   │
│  ├── Redis Cache                           │
│  ├── Load Balancer                         │
│  └── Multiple Instances                    │
└─────────────────────────────────────────────┘
```

## Technology Stack

```
┌──────────────────────────────────────────────┐
│           Technology Stack                    │
├──────────────────────────────────────────────┤
│  Backend                                      │
│  ├── Java 17+                                │
│  ├── Spring Boot 3.x                         │
│  ├── Spring Web (REST)                       │
│  └── JUnit 5 (Testing)                       │
│                                               │
│  Frontend                                     │
│  ├── HTML5                                    │
│  ├── CSS3                                     │
│  └── Vanilla JavaScript                      │
│                                               │
│  Build Tool                                   │
│  └── Maven                                    │
│                                               │
│  Data Storage                                 │
│  └── ArrayList (In-Memory)                   │
│                                               │
│  Documentation                                │
│  └── Markdown                                 │
└──────────────────────────────────────────────┘
```
