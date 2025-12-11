# Banking Service System

## Overview

This is a simple banking system that manages accounts with core operations: deposits, withdrawals, and transaction statement printing. The system maintains transaction history and enforces business rules like positive amounts and sufficient balance checks.

## Architecture

**Entities:**
- `Account` - Implements `AccountService` interface, manages balance and transaction history
- `Transaction` - Immutable record of a transaction (date, amount, resulting balance)

**Service Layer:**
- `AccountService` - Public interface defining banking operations
- `StatementPrinter` - Handles formatting and display of account statements

**Exception Handling:**
- `InvalidAmountException` - Thrown for invalid amounts (zero or negative)
- `InsufficientBalanceException` - Thrown when withdrawal exceeds available balance

## Technical Implementation

### 1. Account Management

The `Account` class implements the `AccountService` interface with three core methods:

**deposite(int amount)**
- Adds money to the account
- Validates amount is positive
- Records transaction with current date

**withdraw(int amount)**
- Removes money from the account
- Validates amount is positive
- Checks sufficient balance
- Records transaction with current date

**printStatement()**
- Displays all transactions in reverse chronological order
- Shows date, amount, and balance for each transaction

### 2. Transaction Recording

All operations are stored in an ArrayList of Transaction objects:
- Each transaction captures the date, amount, and resulting balance
- Deposits are recorded as positive amounts
- Withdrawals are recorded as negative amounts
- Balance is calculated cumulatively

### 3. Statement Format

The output follows this exact format:
```
Date        || Amount || Balance
14/01/2012  || -500   || 2500
13/01/2012  || 2000   || 3000
10/01/2012  || 1000   || 1000
```

Transactions are printed newest first (reverse chronological order).

### 4. Exception Handling

**Invalid Amount Validation:**
```java
if (amount <= 0) {
    throw new InvalidAmountException("Amount must be positive");
}
```

**Balance Validation:**
```java
if (balance < amount) {
    throw new InsufficientBalanceException("Insufficient balance");
}
```

### 5. Performance Considerations

- ArrayList used for in-memory storage (as per requirements)
- Efficient reverse iteration for statement printing
- No unnecessary object creation
- Immutable Transaction objects prevent accidental modifications

## Public Interface

As per requirements, the `Account` class implements this interface:

```java
public interface AccountService {
    void deposite(int amount);
    void withdraw(int amount);
    void printStatement();
}
```

**Note:** The method name "deposite" is intentionally kept as specified in the requirements.

## Running the Application

Run the main demonstration:
```bash
mvn clean compile exec:java -Dexec.mainClass="org.skypay.Main"
```

Expected output:
```
Banking System Demo
-------------------

Deposited 1000 on 10-01-2012
Deposited 2000 on 13-01-2012
Withdrew 500 on 14-01-2012

Account Statement:
-------------------
Date        || Amount || Balance
14/01/2012  || -500   || 2500
13/01/2012  || 2000   || 3000
10/01/2012  || 1000   || 1000
```

## Running Tests

Execute the test suite:
```bash
mvn test
```

The test suite includes:
- 20+ comprehensive tests covering all scenarios
- Acceptance test matching exact requirements
- Exception handling validation
- Edge case testing (zero balance, exact withdrawal, large amounts)
- Statement formatting verification

### Key Test Cases

**Acceptance Test:**
- Deposit 1000 on 10/01/2012
- Deposit 2000 on 13/01/2012
- Withdraw 500 on 14/01/2012
- Verify statement output matches requirements exactly

**Exception Tests:**
- Invalid amounts (negative or zero)
- Insufficient balance scenarios
- Balance integrity after failed operations

**Business Logic Tests:**
- Multiple deposits and withdrawals
- Balance calculation accuracy
- Transaction ordering
- Date formatting

## Design Decisions

1. **Immutable Transactions:** Once created, transactions cannot be modified, ensuring audit trail integrity.

2. **Simple Data Types:** Using `int` for amounts as specified (real systems would use BigDecimal for precision).

3. **ArrayList Storage:** In-memory storage using ArrayList as required (no database persistence).

4. **Separation of Concerns:** Statement printing logic separated from Account class for maintainability.

5. **Method Overloading:** Account provides both interface methods (using current date) and overloaded versions (accepting specific dates for testing).

## Requirements Compliance

All technical requirements have been met:

- [x] Handle exceptions for invalid inputs
- [x] Efficient implementation with performance in mind
- [x] Comprehensive test coverage
- [x] ArrayList-based storage (no repositories)
- [x] Integer amounts for simplicity
- [x] Exact statement format matching requirements
- [x] Cannot modify public interface
- [x] Simplest possible solutions chosen throughout
