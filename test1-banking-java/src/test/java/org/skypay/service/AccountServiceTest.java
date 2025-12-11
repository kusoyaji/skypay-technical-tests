package org.skypay.service;

import org.junit.jupiter.api.Test;
import org.skypay.model.Account;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {
    
    @Test
    public void accountShouldImplementAccountService() {
        Account account = new Account();
        assertTrue(account instanceof AccountService, 
            "Account must implement AccountService interface");
    }
    
    @Test
    public void accountServiceInterfaceShouldHaveDepositeMethod() throws NoSuchMethodException {
        // Verify the interface has the deposite method with correct signature
        AccountService.class.getDeclaredMethod("deposite", int.class);
    }
    
    @Test
    public void accountServiceInterfaceShouldHaveWithdrawMethod() throws NoSuchMethodException {
        // Verify the interface has the withdraw method with correct signature
        AccountService.class.getDeclaredMethod("withdraw", int.class);
    }
    
    @Test
    public void accountServiceInterfaceShouldHavePrintStatementMethod() throws NoSuchMethodException {
        // Verify the interface has the printStatement method with correct signature
        AccountService.class.getDeclaredMethod("printStatement");
    }
    
    @Test
    public void shouldBeAbleToUseAccountThroughInterface() {
        // Verify Account can be used through the interface
        AccountService accountService = new Account();
        
        // Should be able to call all interface methods
        accountService.deposite(1000);
        assertEquals(1000, ((Account) accountService).getBalance());
        
        accountService.withdraw(500);
        assertEquals(500, ((Account) accountService).getBalance());
        
        // Should not throw exception
        assertDoesNotThrow(() -> accountService.printStatement());
    }
}
