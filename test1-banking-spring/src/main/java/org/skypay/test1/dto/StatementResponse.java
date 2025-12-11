package org.skypay.test1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatementResponse {
    
    private int currentBalance;
    private List<TransactionResponse> transactions;
}
