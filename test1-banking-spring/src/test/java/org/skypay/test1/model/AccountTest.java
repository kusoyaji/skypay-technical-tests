package org.skypay.test1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypay.test1.exception.InsufficientBalanceException;
import org.skypay.test1.exception.InvalidAmountException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    
    private Account account;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        account = new Account();
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    void shouldStartWithZeroBalance() {
        assertEquals(0, account.getBalance());
    }
    
    @Test
    void shouldDepositMoneySuccessfully() {
        account.deposite(1000, LocalDate.now());
        assertEquals(1000, account.getBalance());
    }
    
    @Test
    void shouldWithdrawMoneySuccessfully() {
        account.deposite(1000, LocalDate.now());
        account.withdraw(500, LocalDate.now());
        assertEquals(500, account.getBalance());
    }
    
    @Test
    void shouldThrowExceptionWhenDepositingNegativeAmount() {
        assertThrows(InvalidAmountException.class, () -> account.deposite(-100, LocalDate.now()));
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        account.deposite(100, LocalDate.now());
        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(200, LocalDate.now()));
    }
    
    @Test
    void acceptanceTest() {
        // Given: deposit 1000 on 10-01-2012, deposit 2000 on 13-01-2012, withdraw 500 on 14-01-2012
        account.deposite(1000, LocalDate.of(2012, 1, 10));
        account.deposite(2000, LocalDate.of(2012, 1, 13));
        account.withdraw(500, LocalDate.of(2012, 1, 14));
        
        // When
        account.printStatement();
        
        // Then
        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals("Date        || Amount || Balance", lines[0]);
        assertTrue(lines[1].contains("14/01/2012"));
        assertTrue(lines[1].contains("-500"));
        assertTrue(lines[1].contains("2500"));
        assertTrue(lines[2].contains("13/01/2012"));
        assertTrue(lines[2].contains("2000"));
        assertTrue(lines[2].contains("3000"));
        assertTrue(lines[3].contains("10/01/2012"));
        assertTrue(lines[3].contains("1000"));
    }
    
    @org.junit.jupiter.api.AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }
}
