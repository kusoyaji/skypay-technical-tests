package org.skypay.test2.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a user in the hotel reservation system.
 */
public class User {
    private final int userId;
    private int balance;
    private final LocalDateTime createdAt;

    public User(int userId, int balance) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    public int getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public void deductBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to deduct must be positive");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance -= amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                '}';
    }
}
