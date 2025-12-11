package org.skypay.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    
    @Test
    void shouldCreateTransactionWithCorrectValues() {
        LocalDate date = LocalDate.of(2012, 1, 10);
        int amount = 1000;
        int balance = 1000;
        
        Transaction transaction = new Transaction(date, amount, balance);
        
        assertEquals(date, transaction.getDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals(balance, transaction.getBalance());
    }
    
    @Test
    void shouldHandleNegativeAmounts() {
        Transaction transaction = new Transaction(
            LocalDate.of(2012, 1, 14),
            -500,
            2500
        );
        
        assertEquals(-500, transaction.getAmount());
    }
    
    @Test
    void shouldHandleZeroBalance() {
        Transaction transaction = new Transaction(
            LocalDate.now(),
            -1000,
            0
        );
        
        assertEquals(0, transaction.getBalance());
    }
    
    @Test
    void shouldStoreDateCorrectly() {
        LocalDate specificDate = LocalDate.of(2012, 1, 10);
        Transaction transaction = new Transaction(specificDate, 100, 100);
        
        assertEquals(specificDate, transaction.getDate());
        assertEquals(2012, transaction.getDate().getYear());
        assertEquals(1, transaction.getDate().getMonthValue());
        assertEquals(10, transaction.getDate().getDayOfMonth());
    }
}
