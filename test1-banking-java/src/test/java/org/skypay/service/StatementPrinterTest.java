package org.skypay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skypay.model.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatementPrinterTest {
    
    private StatementPrinter printer;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        printer = new StatementPrinter();
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    void shouldPrintHeaderForEmptyTransactionList() {
        List<Transaction> transactions = new ArrayList<>();
        
        printer.print(transactions);
        
        String output = outputStream.toString();
        assertTrue(output.contains("Date        || Amount || Balance"));
    }
    
    @Test
    void shouldPrintSingleTransaction() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2012, 1, 10), 1000, 1000));
        
        printer.print(transactions);
        
        String output = outputStream.toString();
        assertTrue(output.contains("10/01/2012"));
        assertTrue(output.contains("1000"));
    }
    
    @Test
    void shouldPrintMultipleTransactionsInReverseOrder() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2012, 1, 10), 1000, 1000));
        transactions.add(new Transaction(LocalDate.of(2012, 1, 13), 2000, 3000));
        transactions.add(new Transaction(LocalDate.of(2012, 1, 14), -500, 2500));
        
        printer.print(transactions);
        
        String output = outputStream.toString();
        String[] lines = output.split(System.lineSeparator());
        
        assertEquals("Date        || Amount || Balance", lines[0]);
        assertTrue(lines[1].contains("14/01/2012"));
        assertTrue(lines[2].contains("13/01/2012"));
        assertTrue(lines[3].contains("10/01/2012"));
    }
    
    @Test
    void shouldFormatNegativeAmountsCorrectly() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2012, 1, 14), -500, 2500));
        
        printer.print(transactions);
        
        String output = outputStream.toString();
        assertTrue(output.contains("-500"));
    }
    
    @Test
    void shouldFormatDatesCorrectly() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2023, 12, 25), 100, 100));
        
        printer.print(transactions);
        
        String output = outputStream.toString();
        assertTrue(output.contains("25/12/2023"));
    }
    
    @org.junit.jupiter.api.AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }
}
