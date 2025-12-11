package org.skypay.test1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
