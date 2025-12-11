package org.skypay.model;

import org.skypay.exception.InsufficientBalanceException;
import org.skypay.exception.InvalidAmountException;
import org.skypay.service.AccountService;
import org.skypay.service.StatementPrinter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Account implements AccountService {
    
    private int balance;
    private final List<Transaction> transactions;
    private final StatementPrinter statementPrinter;
    
    public Account() {
        this.balance = 0;
        this.transactions = new ArrayList<>();
        this.statementPrinter = new StatementPrinter();
    }
    
    @Override
    public void deposite(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive, received: " + amount);
        }
        
        balance += amount;
        transactions.add(new Transaction(LocalDate.now(), amount, balance));
    }
    
    // Overload for testing with specific dates
    public void deposite(int amount, LocalDate date) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive, received: " + amount);
        }
        
        balance += amount;
        transactions.add(new Transaction(date, amount, balance));
    }
    
    @Override
    public void withdraw(int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive, received: " + amount);
        }
        
        if (balance < amount) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: " + balance + ", requested: " + amount
            );
        }
        
        balance -= amount;
        transactions.add(new Transaction(LocalDate.now(), -amount, balance));
    }
    
    // Overload for testing with specific dates
    public void withdraw(int amount, LocalDate date) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive, received: " + amount);
        }
        
        if (balance < amount) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: " + balance + ", requested: " + amount
            );
        }
        
        balance -= amount;
        transactions.add(new Transaction(date, -amount, balance));
    }
    
    @Override
    public void printStatement() {
        statementPrinter.print(transactions);
    }
    
    // For testing purposes
    public int getBalance() {
        return balance;
    }
}
