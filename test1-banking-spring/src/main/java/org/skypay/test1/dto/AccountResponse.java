package org.skypay.test1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponse {
    
    private int balance;
    private String message;
}
