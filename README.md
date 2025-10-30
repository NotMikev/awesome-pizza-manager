# Awesome Pizza - Order Manager

## Summary
1. [Introduction](#introduzione)
2. [Download and Installation](#download-e-installazione)
3. [Project Execution](#esecuzione-del-progetto)
4. [Architecture and Technologies](#architettura-e-tecnologie)
5. [Operational Flow](#flusso-operativo)
6. [API and Documentation](#api-e-documentazione)
7. [Additional Information](#informazioni-aggiuntive)

## Introduction

Awesome Pizza Manager is an application developed in Java/Spring Boot for managing the order flow in a pizzeria. 
The system manages the complete lifecycle of orders, from creation to preparation and delivery, with particular focus on state tracking and operation auditing.

## Download and Installation

### Prerequisites
- Java Development Kit (JDK) 17
- Apache Maven 3.9+
- Git

### Download Procedure
```bash
Clone the repository
git clone https://github.com/NotMikev/awesome-pizza-manager.git

Access the project directory
cd order.manager
```


## Project Execution

### Development Environment Setup
1. Verify Java version:
```bash
java -version
```
Expected output: OpenJDK/Java version 17.x.x

2. Verify Maven installation:
```bash
mvn -version
```
Expected output: Apache Maven 3.9.x

### Run with IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select: `File` → `Open`
3. Select the project root folder (`order.manager`)
4. Wait for IntelliJ to complete Maven import (icon bottom right)
5. Run Configuration:
- Open class `PizzaOrdersManagerApplication`
- Right click → `Run PizzaOrdersManagerApplication`
- Verify log message: `Started PizzaOrdersManagerApplication in X.XXX seconds`

### Run with VS Code
1. VS Code Prerequisites:
- Extension "Extension Pack for Java" (vscjava.vscode-java-pack)
- Extension "Spring Boot Extension Pack" (Pivotal.vscode-spring-boot-pack)

2. Open project:
- `File` → `Open Folder`
- Select folder `order.manager`
- Wait for project indexing (icon bottom right)

3. Start application:
- Open Command Palette (⇧⌘P on macOS, Ctrl+Shift+P on Windows)
- Type: `Spring Boot Dashboard`
- Select `order.manager` and click play button
- Verify in logs: `Started PizzaOrdersManagerApplication`

### Standalone Run
1. Build:
```bash
# From project root directory
./mvnw clean install -DskipTests
```
Verify output: `BUILD SUCCESS`

2. Verify JAR:
```bash
   ls -l target/order.manager-0.0.1-SNAPSHOT.jar
   ```lean install -DskipTests
```
Expected size: ~40MB

3. Run:
```bash
   java -jar target/order.manager-0.0.1-SNAPSHOT.jar
   ```

4. Verify correct startup:
- Wait for log: `Started PizzaOrdersManagerApplication`
- No errors in log
- Test endpoint: http://localhost:8080/awesome/actuator/health
- Expected response: `{"status":"UP"}`

### Installation Check
1. Swagger UI: http://localhost:8080/awesome/swagger-ui.html
2. H2 Console: http://localhost:8080/awesome/h2-console
3. Health Check: http://localhost:8080/awesome/actuator/health

### Common Troubleshooting
- **Port 8080 already in use error**:
```bash
  lsof -i :8080
  kill -9 [PID]
  ```

- **Java version error**:
```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  java -version
  ```
- **Maven error**:
  ```bash
  ./mvnw -X clean install
  ```
  -X For detailed log

## Architecture and Technologies

### Technology Stack
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **API Documentation**: OpenAPI/Swagger 2.8.13
- **Logging**: Logback/Slf4j
- **Testing**: JUnit, Spring Test
- **Libraries**: 
- Lombok 1.18.34
- MapStruct 1.6.3

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/awesome/pizza/order/manager/
│   │       ├── config/        # Configurazioni Spring
│   │       │   ├── OpenApiConfig.java
│   │       │   └── WebConfig.java
│   │       ├── constants/     # Costanti applicative
│   │       ├── controller/    # REST Controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       │   ├── error/     # DTO per errori
│   │       │   └── purchase/  # DTO per ordini
│   │       ├── entity/        # Entità JPA
│   │       ├── exception/     # Gestione Eccezioni
│   │       ├── filter/        # Filtri HTTP
│   │       ├── mapper/        # Object Mappers
│   │       ├── repository/    # Repositories JPA
│   │       └── service/       # Logica di Business
│   └── resources/
│       ├── application.properties # Configurazioni
│       └── logback-spring.xml     # Config logging
```

## Operational Flow

### Order States
1. **NEW**: Order received and registered
2. **IN_PROGRESS**: Order in preparation
3. **READY**: Order ready for delivery

### Operational Workflow
1. Customer places an order → State NEW
2. System assigns a unique code
3. Operator can view all orders in state NEW
4. Operator takes charge → State IN_PROGRESS
5. Operator can view orders by each state (NEW, IN_PROGRESS, READY)
6. Preparation completion → State READY

## API and Documentation

### REST Endpoints

#### Order Management
- **POST** `/awesome/api/purchase`
  - Creates a new order
  - Request body: `{ "pizza": "string" }`
  - Response: PurchaseDto with order code

- **GET** `/awesome/api/purchase/status/{code}`
  - Checks order status
  - Response: PurchaseDto with current status

- **GET** `/awesome/api/purchase/new`
  - Retrieves all orders in state NEW
  - Response: List of PurchaseDto in state NEW

- **GET** `/awesome/api/purchase/status/{status}`
  - Retrieves orders by specific state (NEW, IN_PROGRESS, READY)
  - Response: List of PurchaseDto in requested state
  
- **POST** `/awesome/api/purchase/next`
  - Takes the next order to process
  - Response: PurchaseDto of assigned order
  
- **POST** `/awesome/api/purchase/next/{code}`
  - Takes a specific order
  - Response: PurchaseDto of updated order
  
- **POST** `/awesome/api/purchase/{code}/ready`
  - Marks an order as ready
  - Response: PurchaseDto with READY state

### OpenAPI Documentation
Swagger documentation is available at:
```
http://localhost:8080/awesome/swagger-ui.html
```

## Additional Information

### Current Version
- Version: 0.0.1-SNAPSHOT
- Status: Active Development

### Monitoring
Available Actuator endpoints:
- Health Check: `/awesome/actuator/health`

### Database
- H2 Console: `/awesome/h2-console`
- URL JDBC: `jdbc:h2:mem:awesome-db`
- In-memory database (needed info in application.properties)

### Testing
Run tests:
```bash
./mvnw test
```

The project includes:
- API Integration Tests (`ApiAuditIntegrationTest`)
- Purchase Flow Tests (`PurchaseFlowTest`)
- Take Order Tests (`TakeNextByCodeTest`)
- Application Tests (`PizzaOrdersManagerApplicationTests`)

### Debug Notes
- Detailed logging configured in `logback-spring.xml`
- Application logs in `/logs/app.log` (archived daily by date)
- Pizzeria-specific log in `/logs/awesome-pizza.log`
- Audit log in H2 database

### Contribution
1. Fork the repository
2. Create a branch for your changes
3. Implement changes with related tests
4. Open a Pull Request
