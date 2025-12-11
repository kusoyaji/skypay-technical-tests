package org.skypay.test2.model;

/**
 * Enum representing the different types of hotel rooms.
 */
public enum RoomType {
    STANDARD("Standard Suite"),
    JUNIOR("Junior Suite"),
    MASTER("Master Suite");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
