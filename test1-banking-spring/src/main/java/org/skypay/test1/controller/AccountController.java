package org.skypay.test1.controller;

import jakarta.validation.Valid;
import org.skypay.test1.dto.AccountResponse;
import org.skypay.test1.dto.StatementResponse;
import org.skypay.test1.dto.TransactionRequest;
import org.skypay.test1.dto.TransactionResponse;
import org.skypay.test1.model.Account;
import org.skypay.test1.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    
    private final Account account;
    
    public AccountController(Account account) {
        this.account = account;
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(@Valid @RequestBody TransactionRequest request) {
        if (request.getDate() != null && !request.getDate().isBlank()) {
            account.deposite(request.getAmount(), request.getLocalDate());
        } else {
            account.deposite(request.getAmount());
        }
        return ResponseEntity.ok(
            new AccountResponse(account.getBalance(), "Deposit successful")
        );
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@Valid @RequestBody TransactionRequest request) {
        if (request.getDate() != null && !request.getDate().isBlank()) {
            account.withdraw(request.getAmount(), request.getLocalDate());
        } else {
            account.withdraw(request.getAmount());
        }
        return ResponseEntity.ok(
            new AccountResponse(account.getBalance(), "Withdrawal successful")
        );
    }
    
    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance() {
        return ResponseEntity.ok(account.getBalance());
    }
    
    @GetMapping("/statement")
    public ResponseEntity<StatementResponse> getStatement() {
        List<TransactionResponse> transactionResponses = account.getTransactions()
            .stream()
            .map(t -> new TransactionResponse(t.getDate(), t.getAmount(), t.getBalance()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            new StatementResponse(account.getBalance(), transactionResponses)
        );
    }
    
    @GetMapping("/statement/print")
    public ResponseEntity<String> printStatement() {
        account.printStatement();
        return ResponseEntity.ok("Statement printed to console");
    }
}
