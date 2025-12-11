package org.skypay.test2.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Room entity.
 */
class RoomTest {

    @Test
    void testRoomCreation_Valid() {
        Room room = new Room(101, RoomType.STANDARD, 1000);
        
        assertNotNull(room);
        assertEquals(101, room.getRoomNumber());
        assertEquals(RoomType.STANDARD, room.getRoomType());
        assertEquals(1000, room.getPricePerNight());
        assertNotNull(room.getCreatedAt());
    }

    @Test
    void testRoomCreation_InvalidRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Room(0, RoomType.STANDARD, 1000));
        assertThrows(IllegalArgumentException.class, () -> 
            new Room(-1, RoomType.STANDARD, 1000));
    }

    @Test
    void testRoomCreation_NullRoomType() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Room(101, null, 1000));
    }

    @Test
    void testRoomCreation_InvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Room(101, RoomType.STANDARD, 0));
        assertThrows(IllegalArgumentException.class, () -> 
            new Room(101, RoomType.STANDARD, -100));
    }

    @Test
    void testSetRoomType_Valid() {
        Room room = new Room(101, RoomType.STANDARD, 1000);
        room.setRoomType(RoomType.MASTER);
        
        assertEquals(RoomType.MASTER, room.getRoomType());
    }

    @Test
    void testSetRoomType_Null() {
        Room room = new Room(101, RoomType.STANDARD, 1000);
        
        assertThrows(IllegalArgumentException.class, () -> room.setRoomType(null));
    }

    @Test
    void testSetPricePerNight_Valid() {
        Room room = new Room(101, RoomType.STANDARD, 1000);
        room.setPricePerNight(2000);
        
        assertEquals(2000, room.getPricePerNight());
    }

    @Test
    void testSetPricePerNight_Invalid() {
        Room room = new Room(101, RoomType.STANDARD, 1000);
        
        assertThrows(IllegalArgumentException.class, () -> room.setPricePerNight(0));
        assertThrows(IllegalArgumentException.class, () -> room.setPricePerNight(-100));
    }

    @Test
    void testEquals_SameRoom() {
        Room room1 = new Room(101, RoomType.STANDARD, 1000);
        Room room2 = new Room(101, RoomType.MASTER, 5000);
        
        assertEquals(room1, room2);
    }

    @Test
    void testEquals_DifferentRoom() {
        Room room1 = new Room(101, RoomType.STANDARD, 1000);
        Room room2 = new Room(102, RoomType.STANDARD, 1000);
        
        assertNotEquals(room1, room2);
    }

    @Test
    void testAllRoomTypes() {
        Room standard = new Room(1, RoomType.STANDARD, 1000);
        Room junior = new Room(2, RoomType.JUNIOR, 2000);
        Room master = new Room(3, RoomType.MASTER, 3000);
        
        assertEquals("Standard Suite", standard.getRoomType().getDisplayName());
        assertEquals("Junior Suite", junior.getRoomType().getDisplayName());
        assertEquals("Master Suite", master.getRoomType().getDisplayName());
    }
}
