# Banking Service - Spring Boot 4

## Overview

Spring Boot 4 implementation of a banking service with REST API and a beautiful web interface. The system implements core banking operations (deposit, withdraw, statement) following the AccountService interface specification. Uses in-memory ArrayList storage as per technical requirements.

## Features

- 💰 Deposit and withdraw money
- 📊 View account balance and transaction history
- 🌐 Beautiful web interface for easy testing
- 🔌 RESTful API endpoints
- ⚠️ Exception handling with proper error messages
- ✅ Input validation
- 🎨 Responsive design with modern UI

## Quick Start

### Run the Application

```bash
mvn spring-boot:run
```

Application starts on `http://localhost:8080`

### Access the Web Interface

Open your browser and go to:
```
http://localhost:8080
```

The banking interface provides:
- Real-time balance display
- Deposit form
- Withdrawal form
- Transaction statement table
- Automatic refresh after operations

## Testing the Application

### Option 1: Web Interface (Easiest)

Simply open `http://localhost:8080` in your browser. The interface is fully functional with:
- Beautiful gradient design
- Form validation
- Success/error messages
- Auto-updating balance and transactions
- No setup required

### Option 2: REST Client Extension (VS Code)

1. Install "REST Client" extension by Huachao Mao in VS Code
2. Create a file named `test-api.http`
3. Add these examples:

```http
### Get Balance
GET http://localhost:8080/api/account/balance

### Deposit Money
POST http://localhost:8080/api/account/deposit
Content-Type: application/json

{
  "amount": 1000
}

### Withdraw Money
POST http://localhost:8080/api/account/withdraw
Content-Type: application/json

{
  "amount": 500
}

### Get Statement (JSON)
GET http://localhost:8080/api/account/statement

### Print Statement to Console
GET http://localhost:8080/api/account/statement/print
```

### Option 3: Postman

Import these requests in Postman:

**Deposit:**
```
POST http://localhost:8080/api/account/deposit
Headers: Content-Type: application/json
Body: {"amount": 1000}
```

**Withdraw:**
```
POST http://localhost:8080/api/account/withdraw
Headers: Content-Type: application/json
Body: {"amount": 500}
```

**Get Balance:**
```
GET http://localhost:8080/api/account/balance
```

**Get Statement:**
```
GET http://localhost:8080/api/account/statement
```

## API Documentation

### Endpoints

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/api/account/deposit` | Deposit money | `{"amount": 1000}` | `{"balance": 1000, "message": "Deposit successful"}` |
| POST | `/api/account/withdraw` | Withdraw money | `{"amount": 500}` | `{"balance": 500, "message": "Withdrawal successful"}` |
| GET | `/api/account/balance` | Get current balance | - | `1000` |
| GET | `/api/account/statement` | Get transaction history | - | `{"currentBalance": 1000, "transactions": [...]}` |
| GET | `/api/account/statement/print` | Print formatted statement to console | - | `"Statement printed to console"` |

### Error Responses

All errors return:
```json
{
  "timestamp": "2025-12-11T23:05:00.958792500",
  "message": "Error description",
  "details": "uri=/api/account/..."
}
```

**Common errors:**
- `400 Bad Request` - Invalid amount (zero or negative)
- `400 Bad Request` - Insufficient balance for withdrawal

## Project Structure

```
test1-banking-spring/
├── src/
│   ├── main/
│   │   ├── java/org/skypay/test1/
│   │   │   ├── controller/
│   │   │   │   ├── AccountController.java (REST API)
│   │   │   │   └── HomeController.java (Static page serving)
│   │   │   ├── dto/
│   │   │   │   ├── AccountResponse.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── StatementResponse.java
│   │   │   │   ├── TransactionRequest.java
│   │   │   │   └── TransactionResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── InsufficientBalanceException.java
│   │   │   │   └── InvalidAmountException.java
│   │   │   ├── model/
│   │   │   │   ├── Account.java (implements AccountService)
│   │   │   │   └── Transaction.java
│   │   │   ├── service/
│   │   │   │   ├── AccountService.java (interface)
│   │   │   │   └── StatementPrinter.java
│   │   │   └── Test1BankingSpringApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   └── index.html (Web interface with styling)
│   │       └── application.properties
│   └── test/
│       └── java/org/skypay/test1/
│           ├── acceptance/
│           │   └── AcceptanceTest.java
│           ├── controller/
│           │   └── AccountControllerIntegrationTest.java
│           ├── model/
│           │   └── AccountTest.java
│           └── Test1BankingSpringApplicationTests.java
├── pom.xml
└── README.md
```

## Running Tests

```bash
mvn test
```

All tests should pass, including:
- Unit tests for Account model
- Acceptance tests for core functionality
- Integration tests for API endpoints

## Technologies

- **Spring Boot**: 4.0.0
- **Java**: 17
- **Lombok**: Reduces boilerplate code
- **Jakarta Validation**: Request validation
- **JUnit 5**: Testing framework
- **MockMvc**: Controller testing
- **Maven**: Build and dependency management

## Dependencies

```xml
<dependencies>
    <!-- Spring Boot Web (includes REST, MVC) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Design Decisions

### No Database
Uses in-memory ArrayList storage per requirements. This keeps the service lightweight and focused on core banking logic.

### HomeController
The `HomeController` forwards the root path (`/`) to the static `index.html` page. This ensures the web interface loads when you navigate to `http://localhost:8080`.

### Exception Handling
Global exception handler (`GlobalExceptionHandler`) catches all exceptions and returns consistent JSON error responses with timestamps.

### DTOs (Data Transfer Objects)
Separate DTOs for requests and responses keep the API clean and allow for validation without coupling to the domain model.

### AccountService Interface
The `AccountService` interface preserves the original method signature including the "deposite" typo to maintain contract compliance with the specification.

## Requirements Compliance

✅ Account implements AccountService interface  
✅ Public interface preserved (deposite typo as per specification)  
✅ Exception handling for invalid inputs  
✅ ArrayList-based storage (no database required)  
✅ Integer amounts for all operations  
✅ Date format: dd/MM/yyyy with slashes  
✅ Statement in reverse chronological order  
✅ REST API with proper HTTP status codes  
✅ Beautiful web interface with modern styling

## Troubleshooting

### Port Already in Use
If port 8080 is occupied, change it in [application.properties](src/main/resources/application.properties):
```properties
server.port=8081
```

### Web Interface Not Loading
1. Ensure the application is running (`mvn spring-boot:run`)
2. Check the console for any startup errors
3. Visit `http://localhost:8080` (or your configured port)
4. Clear browser cache if needed

### API Returns 404
Verify the endpoint paths:
- API endpoints: `/api/account/*`
- Web interface: `/` (root)

### Application Won't Start
Check:
- Java 17 is installed: `java -version`
- Maven is installed: `mvn -version`
- No other application is using port 8080

---

Built with ❤️ using Spring Boot 4
