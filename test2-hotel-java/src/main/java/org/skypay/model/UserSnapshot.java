package org.skypay.model;

/**
 * Immutable snapshot of User state at booking time.
 * Preserves the user's balance when the booking was made.
 */
public class UserSnapshot {
    private final int userId;
    private final int balanceAtBooking;

    public UserSnapshot(User user) {
        this.userId = user.getUserId();
        this.balanceAtBooking = user.getBalance();
    }

    public int getUserId() {
        return userId;
    }

    public int getBalanceAtBooking() {
        return balanceAtBooking;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", balance=" + balanceAtBooking +
                '}';
    }
}
