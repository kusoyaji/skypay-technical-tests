package org.skypay;

import org.skypay.model.Account;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Banking System Demo");
        System.out.println("-------------------");
        System.out.println();
        
        // Create a new account
        Account account = new Account();
        
        // Acceptance test scenario:
        // Client makes a deposit of 1000 on 10-01-2012
        account.deposite(1000, LocalDate.of(2012, 1, 10));
        System.out.println("Deposited 1000 on 10-01-2012");
        
        // And a deposit of 2000 on 13-01-2012
        account.deposite(2000, LocalDate.of(2012, 1, 13));
        System.out.println("Deposited 2000 on 13-01-2012");
        
        // And a withdrawal of 500 on 14-01-2012
        account.withdraw(500, LocalDate.of(2012, 1, 14));
        System.out.println("Withdrew 500 on 14-01-2012");
        
        System.out.println();
        System.out.println("Account Statement:");
        System.out.println("-------------------");
        
        // When they print their bank statement
        account.printStatement();
    }
}