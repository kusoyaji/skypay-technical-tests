package org.skypay.model;

/**
 * Immutable snapshot of Room state at booking time.
 * This ensures that setRoom() changes don't affect existing bookings,
 * satisfying the technical requirement: "setRoom(...) should not impact previously created bookings"
 */
public class RoomSnapshot {
    private final int roomNumber;
    private final RoomType roomType;
    private final int pricePerNight;

    public RoomSnapshot(Room room) {
        this.roomNumber = room.getRoomNumber();
        this.roomType = room.getRoomType();
        this.pricePerNight = room.getPricePerNight();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", roomType=" + roomType +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
