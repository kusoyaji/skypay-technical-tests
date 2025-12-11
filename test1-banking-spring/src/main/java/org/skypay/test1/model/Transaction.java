package org.skypay.test1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Transaction {
    
    private final LocalDate date;
    private final int amount;
    private final int balance;
}
