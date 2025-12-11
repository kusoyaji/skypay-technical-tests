package org.skypay.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypay.exception.InsufficientBalanceException;
import org.skypay.exception.InvalidAmountException;

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
    void shouldDepositMultipleAmounts() {
        account.deposite(500, LocalDate.now());
        account.deposite(300, LocalDate.now());
        account.deposite(200, LocalDate.now());
        
        assertEquals(1000, account.getBalance());
    }
    
    @Test
    void shouldWithdrawMoneySuccessfully() {
        account.deposite(1000, LocalDate.now());
        account.withdraw(500, LocalDate.now());
        
        assertEquals(500, account.getBalance());
    }
    
    @Test
    void shouldWithdrawMultipleTimes() {
        account.deposite(1000, LocalDate.now());
        account.withdraw(200, LocalDate.now());
        account.withdraw(300, LocalDate.now());
        
        assertEquals(500, account.getBalance());
    }
    
    @Test
    void shouldWithdrawExactBalance() {
        account.deposite(1000, LocalDate.now());
        account.withdraw(1000, LocalDate.now());
        
        assertEquals(0, account.getBalance());
    }
    
    @Test
    void shouldThrowExceptionWhenDepositingNegativeAmount() {
        InvalidAmountException exception = assertThrows(
            InvalidAmountException.class,
            () -> account.deposite(-100, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("must be positive"));
    }
    
    @Test
    void shouldThrowExceptionWhenDepositingZero() {
        InvalidAmountException exception = assertThrows(
            InvalidAmountException.class,
            () -> account.deposite(0, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("must be positive"));
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawingNegativeAmount() {
        account.deposite(1000, LocalDate.now());
        
        InvalidAmountException exception = assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(-100, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("must be positive"));
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawingZero() {
        account.deposite(1000, LocalDate.now());
        
        InvalidAmountException exception = assertThrows(
            InvalidAmountException.class,
            () -> account.withdraw(0, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("must be positive"));
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        account.deposite(100, LocalDate.now());
        
        InsufficientBalanceException exception = assertThrows(
            InsufficientBalanceException.class,
            () -> account.withdraw(200, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawingFromEmptyAccount() {
        InsufficientBalanceException exception = assertThrows(
            InsufficientBalanceException.class,
            () -> account.withdraw(100, LocalDate.now())
        );
        
        assertTrue(exception.getMessage().contains("Insufficient balance"));
    }
    
    @Test
    void shouldMaintainBalanceWhenExceptionOccurs() {
        account.deposite(500, LocalDate.now());
        
        try {
            account.withdraw(1000, LocalDate.now());
        } catch (InsufficientBalanceException e) {
            // Expected exception
        }
        
        // Balance should remain unchanged
        assertEquals(500, account.getBalance());
    }
    
    @Test
    void shouldPrintEmptyStatementWhenNoTransactions() {
        account.printStatement();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Date        || Amount || Balance"));
    }
    
    @Test
    void shouldPrintStatementWithSingleDeposit() {
        account.deposite(1000, LocalDate.of(2012, 1, 10));
        account.printStatement();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Date        || Amount || Balance"));
        assertTrue(output.contains("10/01/2012"));
        assertTrue(output.contains("1000"));
    }
    
    @Test
    void shouldPrintStatementInReverseChronologicalOrder() {
        account.deposite(1000, LocalDate.of(2012, 1, 10));
        account.deposite(2000, LocalDate.of(2012, 1, 13));
        account.withdraw(500, LocalDate.of(2012, 1, 14));
        
        account.printStatement();
        
        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());
        
        // Verify header
        assertEquals("Date        || Amount || Balance", lines[0]);
        
        // Verify transactions are in reverse chronological order
        assertTrue(lines[1].contains("14/01/2012"));
        assertTrue(lines[1].contains("-500"));
        assertTrue(lines[1].contains("2500"));
        
        assertTrue(lines[2].contains("13/01/2012"));
        assertTrue(lines[2].contains("2000"));
        assertTrue(lines[2].contains("3000"));
        
        assertTrue(lines[3].contains("10/01/2012"));
        assertTrue(lines[3].contains("1000"));
    }
    
    @Test
    void shouldHandleComplexTransactionSequence() {
        account.deposite(5000, LocalDate.of(2023, 1, 1));
        account.withdraw(1000, LocalDate.of(2023, 1, 5));
        account.deposite(3000, LocalDate.of(2023, 1, 10));
        account.withdraw(2000, LocalDate.of(2023, 1, 15));
        account.deposite(500, LocalDate.of(2023, 1, 20));
        
        assertEquals(5500, account.getBalance());
    }
    
    @Test
    void shouldHandleLargeAmounts() {
        // Test with large amounts to ensure no overflow issues
        account.deposite(1000000, LocalDate.now());
        account.withdraw(500000, LocalDate.now());
        
        assertEquals(500000, account.getBalance());
    }
    
    @Test
    void shouldRecordCorrectBalanceAfterEachTransaction() {
        account.deposite(100, LocalDate.of(2023, 1, 1));
        assertEquals(100, account.getBalance());
        
        account.deposite(50, LocalDate.of(2023, 1, 2));
        assertEquals(150, account.getBalance());
        
        account.withdraw(30, LocalDate.of(2023, 1, 3));
        assertEquals(120, account.getBalance());
        
        account.withdraw(20, LocalDate.of(2023, 1, 4));
        assertEquals(100, account.getBalance());
    }
    
    @Test
    void acceptanceTest() {
        // Given: deposit 1000 on 10-01-2012, deposit 2000 on 13-01-2012, withdraw 500 on 14-01-2012
        // Given
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
