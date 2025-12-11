Design Questions (Bonus)  
 
1/- Suppose we put all the functions inside the same service. Is this the 
recommended approach ? Please explain. 
 

Answer:     
    Single service is not the recommended approach, because it has many problems such as:
        1- Violates single responsibility principle (SRP) making the service hard to change and to manage it's buisiness logic
        2- it produced Tight coupling because the components cannot be reused independently
        3- Scalability, optimisation and distribution (deployement) issues because the services are not independent              
    Instead we should use specialized services (roomService, UserService, reportService...), because each service has one clear purpose and is easy to test, can change logic easily because of independency, more maintanable and clearer to understand.

2/- In this design, we chose to have a function setRoom(..) that should 
not impact the previous bookings. What is another way ? What is your 
recommendation ? Please explain and justify.


Answer: 
    Keep the current snapshot pattern, it's the best approach for hotel systems. because The Booking entity stores complete snapshots of room and user data at booking time:
                            public class Booking {
                            // Snapshots (immutable)
                            private final int userBalanceAtBooking;
                            private final RoomType roomTypeAtBooking;
                            private final int roomPricePerNightAtBooking;
                            }
    the current pattern (snapshot) is my good because it aligns with Buisiness logic (immutability & contracts), and because it's simple, optimized and better for logging (history) purposes.
    for a real production/enterprise level project, i would add event logging, and schedulers (automate repetitive tasks) to avoid redunduncies or incosistencies
    
    in a previous project i was tasked with making an equipment reservation layer (microservice), so i used the same logic along with complex calculation that were required so that i could get the correct equipment availability and count from other microservices.
