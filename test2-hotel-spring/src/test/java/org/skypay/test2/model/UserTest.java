package org.skypay.test2.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User entity.
 */
class UserTest {

    @Test
    void testUserCreation_Valid() {
        User user = new User(1, 5000);
        
        assertNotNull(user);
        assertEquals(1, user.getUserId());
        assertEquals(5000, user.getBalance());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testUserCreation_InvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> new User(0, 5000));
        assertThrows(IllegalArgumentException.class, () -> new User(-1, 5000));
    }

    @Test
    void testUserCreation_NegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> new User(1, -100));
    }

    @Test
    void testUserCreation_ZeroBalance() {
        User user = new User(1, 0);
        assertEquals(0, user.getBalance());
    }

    @Test
    void testSetBalance_Valid() {
        User user = new User(1, 5000);
        user.setBalance(10000);
        
        assertEquals(10000, user.getBalance());
    }

    @Test
    void testSetBalance_Negative() {
        User user = new User(1, 5000);
        
        assertThrows(IllegalArgumentException.class, () -> user.setBalance(-100));
    }

    @Test
    void testDeductBalance_Valid() {
        User user = new User(1, 5000);
        user.deductBalance(2000);
        
        assertEquals(3000, user.getBalance());
    }

    @Test
    void testDeductBalance_InsufficientBalance() {
        User user = new User(1, 1000);
        
        assertThrows(IllegalArgumentException.class, () -> user.deductBalance(2000));
    }

    @Test
    void testDeductBalance_NegativeAmount() {
        User user = new User(1, 5000);
        
        assertThrows(IllegalArgumentException.class, () -> user.deductBalance(-100));
    }

    @Test
    void testDeductBalance_ExactAmount() {
        User user = new User(1, 1000);
        user.deductBalance(1000);
        
        assertEquals(0, user.getBalance());
    }

    @Test
    void testEquals_SameUser() {
        User user1 = new User(1, 5000);
        User user2 = new User(1, 10000);
        
        assertEquals(user1, user2);
    }

    @Test
    void testEquals_DifferentUser() {
        User user1 = new User(1, 5000);
        User user2 = new User(2, 5000);
        
        assertNotEquals(user1, user2);
    }
}
