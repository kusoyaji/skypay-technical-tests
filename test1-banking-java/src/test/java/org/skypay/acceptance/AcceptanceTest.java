package org.skypay.acceptance;

import org.junit.jupiter.api.Test;
import org.skypay.model.Account;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTest {
    
    @Test
    public void testBankingServiceAcceptanceCriteria() {
        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // Given a client makes a deposit of 1000 on 10-01-2012
            Account account = new Account();
            account.deposite(1000, LocalDate.of(2012, 1, 10));
            
            // And a deposit of 2000 on 13-01-2012
            account.deposite(2000, LocalDate.of(2012, 1, 13));
            
            // And a withdrawal of 500 on 14-01-2012
            account.withdraw(500, LocalDate.of(2012, 1, 14));
            
            // When they print their bank statement
            account.printStatement();
            
            // Then they should see
            String output = outputStream.toString();
            String[] lines = output.split(System.lineSeparator());
            
            // Verify we have exactly 4 lines (header + 3 transactions)
            assertEquals(4, lines.length);
            
            // Verify header
            assertEquals("Date        || Amount || Balance", lines[0]);
            
            // Verify transactions in reverse chronological order
            // Latest transaction first (14/01/2012)
            assertTrue(lines[1].contains("14/01/2012"));
            assertTrue(lines[1].contains("-500"));
            assertTrue(lines[1].contains("2500"));
            
            // Second transaction (13/01/2012)
            assertTrue(lines[2].contains("13/01/2012"));
            assertTrue(lines[2].contains("2000"));
            assertTrue(lines[2].contains("3000"));
            
            // First transaction (10/01/2012)
            assertTrue(lines[3].contains("10/01/2012"));
            assertTrue(lines[3].contains("1000"));
            assertTrue(lines[3].contains("1000"));
            
            // Verify final balance is correct
            assertEquals(2500, account.getBalance());
            
        } finally {
            System.setOut(originalOut);
        }
    }
}
