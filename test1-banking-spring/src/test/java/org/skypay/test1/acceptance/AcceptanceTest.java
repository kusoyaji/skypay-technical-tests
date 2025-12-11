package org.skypay.test1.acceptance;

import org.junit.jupiter.api.Test;
import org.skypay.test1.model.Account;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptanceTest {
    
    @Test
    public void testBankingServiceAcceptanceCriteria() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            Account account = new Account();
            account.deposite(1000, LocalDate.of(2012, 1, 10));
            account.deposite(2000, LocalDate.of(2012, 1, 13));
            account.withdraw(500, LocalDate.of(2012, 1, 14));
            
            account.printStatement();
            
            String output = outputStream.toString();
            String[] lines = output.split(System.lineSeparator());
            
            assertEquals(4, lines.length);
            assertEquals("Date        || Amount || Balance", lines[0]);
            
            assertTrue(lines[1].contains("14/01/2012"));
            assertTrue(lines[1].contains("-500"));
            assertTrue(lines[1].contains("2500"));
            
            assertTrue(lines[2].contains("13/01/2012"));
            assertTrue(lines[2].contains("2000"));
            assertTrue(lines[2].contains("3000"));
            
            assertTrue(lines[3].contains("10/01/2012"));
            assertTrue(lines[3].contains("1000"));
            assertTrue(lines[3].contains("1000"));
            
            assertEquals(2500, account.getBalance());
            
        } finally {
            System.setOut(originalOut);
        }
    }
}
