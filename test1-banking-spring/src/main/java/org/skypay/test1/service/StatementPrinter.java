package org.skypay.test1.service;

import org.skypay.test1.model.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatementPrinter {
    
    private static final String HEADER = "Date        || Amount || Balance";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public void print(List<Transaction> transactions) {
        System.out.println(HEADER);
        
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            String line = formatTransaction(transaction);
            System.out.println(line);
        }
    }
    
    private String formatTransaction(Transaction transaction) {
        String date = transaction.getDate().format(DATE_FORMATTER);
        String amount = String.valueOf(transaction.getAmount());
        String balance = String.valueOf(transaction.getBalance());
        
        return String.format("%-12s|| %-7s|| %s", date + "  ", amount + "   ", balance);
    }
}
