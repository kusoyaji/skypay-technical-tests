package org.skypay.acceptance;

import org.junit.jupiter.api.Test;
import org.skypay.model.Account;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OutputFormatTest {
    
    @Test
    public void testExactOutputFormat() {
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
            
            // Verify output contains all required elements
            assertTrue(output.contains("Date"), "Output must contain 'Date'");
            assertTrue(output.contains("Amount"), "Output must contain 'Amount'");
            assertTrue(output.contains("Balance"), "Output must contain 'Balance'");
            assertTrue(output.contains("14/01/2012"), "Output must contain date 14/01/2012");
            assertTrue(output.contains("13/01/2012"), "Output must contain date 13/01/2012");
            assertTrue(output.contains("10/01/2012"), "Output must contain date 10/01/2012");
            assertTrue(output.contains("-500"), "Output must contain amount -500");
            assertTrue(output.contains("2000"), "Output must contain amount 2000");
            assertTrue(output.contains("2500"), "Output must contain balance 2500");
            assertTrue(output.contains("3000"), "Output must contain balance 3000");
            
            // Verify line count
            String[] lines = output.split(System.lineSeparator());
            assertEquals(4, lines.length, "Statement must have 4 lines (header + 3 transactions)");
            
            // Verify header format
            String header = lines[0];
            assertTrue(header.contains("||"), "Header must contain || separators");
            
            // Verify each transaction line has proper separators
            for (int i = 1; i < lines.length; i++) {
                assertTrue(lines[i].contains("||"), 
                    "Transaction line " + i + " must contain || separators");
            }
            
            // Verify reverse chronological order
            int index14 = output.indexOf("14/01/2012");
            int index13 = output.indexOf("13/01/2012");
            int index10 = output.indexOf("10/01/2012");
            
            assertTrue(index14 < index13, "14/01/2012 must appear before 13/01/2012");
            assertTrue(index13 < index10, "13/01/2012 must appear before 10/01/2012");
            
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    public void testDateFormatUsesSlashes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            Account account = new Account();
            account.deposite(100, LocalDate.of(2023, 12, 25));
            account.printStatement();
            
            String output = outputStream.toString();
            
            // Must use slashes, not dashes
            assertTrue(output.contains("25/12/2023"), "Date must use slashes (/)");
            assertFalse(output.contains("25-12-2023"), "Date must not use dashes (-)");
            
        } finally {
            System.setOut(originalOut);
        }
    }
}
