# Design Questions - Answers

## Question 1: Should we put all functions inside the same service?

### Answer: No, this is not the recommended approach for production systems.

### Current Implementation
The current design uses a single `Service` class containing all business logic for rooms, users, and bookings. This was done to strictly follow the requirements specification.

### Issues with Single Service Approach

1. **Violates Single Responsibility Principle (SRP)**
   - The Service class has multiple reasons to change
   - It handles room management, user management, AND booking logic
   - Each responsibility should be in its own class

2. **Low Cohesion**
   - Methods related to different entities are mixed together
   - Hard to locate specific functionality
   - Difficult to understand class purpose

3. **Tight Coupling**
   - All logic is interdependent
   - Cannot reuse individual components
   - Changes ripple across the entire class

4. **Testing Challenges**
   - Hard to test individual concerns in isolation
   - Cannot mock dependencies effectively
   - Tests become complex and fragile

5. **Scalability Issues**
   - Cannot optimize individual services
   - Cannot distribute services across servers
   - Performance tuning affects everything

### Recommended Production Approach

```java
@Service
public class RoomService {
    private final ArrayList<Room> rooms = new ArrayList<>();
    
    public void createOrUpdateRoom(int roomNumber, RoomType type, int price) { }
    public Room findRoomByNumber(int roomNumber) { }
    public List<Room> getAllRoomsSorted() { }
}

@Service
public class UserService {
    private final ArrayList<User> users = new ArrayList<>();
    
    public void createOrUpdateUser(int userId, int balance) { }
    public User findUserById(int userId) { }
    public void deductBalance(User user, int amount) { }
    public List<User> getAllUsersSorted() { }
}

@Service
public class BookingService {
    private final ArrayList<Booking> bookings = new ArrayList<>();
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;
    
    public void bookRoom(int userId, int roomNumber, Date checkIn, Date checkOut) {
        // Delegates to roomService and userService
    }
    
    public boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) { }
    public List<Booking> getAllBookingsSorted() { }
}

@Service
public class ReportService {
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingService bookingService;
    
    public void printAll() { }
    public void printAllUsers() { }
}
```

### Benefits of Service Separation

1. **Single Responsibility**
   - Each service has one clear purpose
   - Easy to understand and maintain
   - Changes are localized

2. **Better Testing**
   ```java
   @Test
   void testBooking() {
       RoomService mockRoomService = mock(RoomService.class);
       UserService mockUserService = mock(UserService.class);
       
       BookingService bookingService = new BookingService(
           mockRoomService, 
           mockUserService
       );
       
       // Test only booking logic in isolation
   }
   ```

3. **Flexibility**
   - Can swap implementations easily
   - Can add caching per service
   - Can apply different security rules

4. **Maintainability**
   - Changes to room logic don't affect user logic
   - Easier to locate bugs
   - Clearer code organization

5. **Scalability**
   - Can deploy services separately (microservices)
   - Can scale services independently
   - Can optimize each service differently

### Conclusion

**For this technical test:** A single service is acceptable and meets requirements.

**For production systems:** Always separate concerns into multiple services following SOLID principles.

---

## Question 2: Alternative approaches to setRoom() not impacting bookings

### Current Design: Snapshot Pattern

The current implementation stores complete snapshots of room and user data within the Booking entity at the time of booking:

```java
public class Booking {
    // Snapshots of data at booking time
    private final int userBalanceAtBooking;
    private final RoomType roomTypeAtBooking;
    private final int roomPricePerNightAtBooking;
}
```

**How it works:**
- When a booking is created, we capture the current state of the room
- Room can be updated later via setRoom()
- Booking retains original values forever
- No link between booking and current room state

### Alternative 1: Room Versioning System

```java
public class Room {
    private int roomNumber;
    private int version;  // Incremented on each change
    private RoomType roomType;
    private int pricePerNight;
    private boolean active;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
}

public class Booking {
    private int roomNumber;
    private int roomVersion;  // References specific version
}

// Keep all room versions
ArrayList<Room> roomHistory;
```

**Implementation:**
```java
public void setRoom(int roomNumber, RoomType type, int price) {
    Room currentVersion = findActiveRoom(roomNumber);
    
    if (currentVersion != null) {
        currentVersion.setActive(false);
        currentVersion.setValidUntil(LocalDateTime.now());
    }
    
    Room newVersion = new Room(
        roomNumber,
        currentVersion != null ? currentVersion.getVersion() + 1 : 1,
        type,
        price
    );
    newVersion.setActive(true);
    newVersion.setValidFrom(LocalDateTime.now());
    
    roomHistory.add(newVersion);
}

public Room getRoomForBooking(int roomNumber, int version) {
    return roomHistory.stream()
        .filter(r -> r.getRoomNumber() == roomNumber && r.getVersion() == version)
        .findFirst()
        .orElse(null);
}
```

**Pros:**
- Complete audit trail of all changes
- Can reconstruct exact state at any point in time
- Can compare versions to see what changed
- Supports complex queries like "show all bookings for room when it was a Standard Suite"

**Cons:**
- More complex implementation
- Higher memory usage (stores all versions)
- More complex queries (need to filter by version)
- Database indexes become more complex

### Alternative 2: Immutable Room Pattern

```java
public class Room {
    private final int roomNumber;
    private final RoomType roomType;
    private final int pricePerNight;
    private final LocalDateTime createdAt;
    private final LocalDateTime deprecatedAt;  // When replaced
    private final Room supersededBy;  // Link to new version
}

public void setRoom(int roomNumber, RoomType type, int price) {
    Room oldRoom = findCurrentRoom(roomNumber);
    
    Room newRoom = new Room(roomNumber, type, price);
    rooms.add(newRoom);
    
    if (oldRoom != null) {
        oldRoom.setDeprecatedAt(LocalDateTime.now());
        oldRoom.setSupersededBy(newRoom);
    }
}
```

**Pros:**
- True immutability (thread-safe)
- Perfect historical tracking
- Can traverse version chain

**Cons:**
- Growing list of inactive rooms
- Complex lookup logic
- Need to filter "active" vs "historical" rooms in every query

### Alternative 3: Price Agreement Pattern

```java
public class Booking {
    private int roomNumber;
    private int agreedPricePerNight;  // Negotiated/booked price
    private RoomType bookedRoomType;  // What they booked
    // No other room snapshots
}

public void setRoom(int roomNumber, RoomType type, int price) {
    Room room = findRoom(roomNumber);
    if (room != null) {
        // Simply update the room
        room.setRoomType(type);
        room.setPricePerNight(price);
    } else {
        rooms.add(new Room(roomNumber, type, price));
    }
}
```

**Pros:**
- Simplest implementation
- Rooms and bookings are completely independent
- Easy to understand
- Minimal memory usage

**Cons:**
- Limited historical context
- Can't see what room actually looked like at booking time
- Potential business logic issues (what if room type changed significantly?)
- Audit trail is incomplete

### Alternative 4: Event Sourcing Pattern

```java
// Instead of storing state, store events
public class RoomCreatedEvent {
    int roomNumber;
    RoomType type;
    int price;
    LocalDateTime timestamp;
}

public class RoomUpdatedEvent {
    int roomNumber;
    RoomType newType;
    int newPrice;
    LocalDateTime timestamp;
}

public class BookingCreatedEvent {
    int bookingId;
    int userId;
    int roomNumber;
    LocalDateTime timestamp;
    // Snapshot of room/user at this time
}

ArrayList<Event> eventStore;

// Rebuild current state by replaying events
public Room getCurrentRoomState(int roomNumber) {
    return eventStore.stream()
        .filter(e -> e.affects(roomNumber))
        .reduce(this::applyEvent);
}
```

**Pros:**
- Complete audit trail
- Can replay to any point in time
- Can answer "what if" questions
- Industry standard for financial systems

**Cons:**
- Most complex implementation
- Requires event replay for queries
- Higher learning curve
- Potential performance issues

## My Recommendation: Enhanced Snapshot Pattern

For this hotel reservation system, I recommend **keeping the snapshot pattern** with minor enhancements:

### Why Snapshots Win

1. **Business Logic Alignment**
   - In hotels, bookings are contracts
   - What was agreed upon shouldn't change
   - Guest booked "Standard Suite for $1000" - that's immutable

2. **Simplicity**
   - Easy to understand
   - Direct data access
   - No complex queries

3. **Performance**
   - Immediate access to booking details
   - No joins or version lookups
   - Scales well

4. **Audit Compliance**
   - Many jurisdictions require exact booking records
   - Cannot modify historical transactions
   - Clear paper trail

### Production Enhancement

For a real production system, I would add:

```java
@Service
public class RoomService {
    // Main room list
    ArrayList<Room> rooms;
    
    // Event log for changes
    ArrayList<RoomChangeEvent> changeHistory;
    
    public void setRoom(int roomNumber, RoomType type, int price) {
        Room room = findRoom(roomNumber);
        
        if (room != null) {
            // Log the change
            changeHistory.add(new RoomChangeEvent(
                roomNumber,
                room.getRoomType(),
                type,
                room.getPricePerNight(),
                price,
                LocalDateTime.now(),
                getCurrentUser()
            ));
            
            // Update room
            room.setRoomType(type);
            room.setPricePerNight(price);
        } else {
            rooms.add(new Room(roomNumber, type, price));
            changeHistory.add(new RoomCreatedEvent(...));
        }
    }
    
    public List<RoomChangeEvent> getRoomHistory(int roomNumber) {
        return changeHistory.stream()
            .filter(e -> e.getRoomNumber() == roomNumber)
            .collect(Collectors.toList());
    }
}
```

This provides:
- ✅ Fast booking queries (snapshots)
- ✅ Complete audit trail (event log)
- ✅ Compliance with regulations
- ✅ Ability to answer "what changed and when"
- ✅ Simple implementation

### When to Use Alternatives

**Use Versioning if:**
- Need to compare specific versions frequently
- Complex reporting on historical data
- Regulatory requirements for version tracking

**Use Immutability if:**
- Regulatory compliance requires never deleting data
- Multi-threaded environment needs thread safety
- Blockchain-like immutability is required

**Use Price Agreement if:**
- Only price matters (budget hotels)
- Room attributes are truly independent
- Simplicity is paramount

**Use Event Sourcing if:**
- Building a financial/banking system
- Need time-travel debugging
- Implementing CQRS architecture
- Have complex business workflows

### Final Recommendation

**For this hotel system:**

Keep the current snapshot design because:
1. Hotel bookings are contracts - what was booked is what matters
2. Guests expect their booking details to remain unchanged
3. Simple to implement and understand
4. Excellent performance
5. Meets all business requirements

**Enhance with:**
- Change event logging for audit trails
- Soft deletes instead of hard deletes
- User/timestamp tracking on all changes

This gives you the best of both worlds: simplicity + compliance + auditability.

---

## Summary

### Question 1 Answer
**No**, putting all functions in one service is not recommended. Separate into RoomService, UserService, BookingService, and ReportService for better maintainability, testability, and scalability.

### Question 2 Answer
**Keep the snapshot pattern**. It's the best fit for hotel reservation systems because bookings are contracts that should be immutable. Enhance with change event logging for audit trails and compliance.
