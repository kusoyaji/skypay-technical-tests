package org.skypay.test1.service;

public interface AccountService {
    
    // Note: typo in method name is intentional per requirements
    void deposite(int amount);
    
    void withdraw(int amount);
    
    void printStatement();
}
