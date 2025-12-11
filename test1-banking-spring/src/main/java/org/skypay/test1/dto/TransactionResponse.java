package org.skypay.test1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionResponse {
    
    private LocalDate date;
    private int amount;
    private int balance;
}
