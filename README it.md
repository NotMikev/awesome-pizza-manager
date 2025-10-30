# Awesome Pizza - Gestore Ordini

## Sommario
1. [Introduzione](#introduzione)
2. [Download e Installazione](#download-e-installazione)
3. [Esecuzione del Progetto](#esecuzione-del-progetto)
4. [Architettura e Tecnologie](#architettura-e-tecnologie)
5. [Flusso Operativo](#flusso-operativo)
6. [API e Documentazione](#api-e-documentazione)
7. [Informazioni Aggiuntive](#informazioni-aggiuntive)

## Introduzione

Awesome Pizza Manager è un'applicazione  sviluppata in Java/Spring Boot per la gestione del flusso degli ordini in una pizzeria. 
Il sistema gestisce il ciclo di vita completo degli ordini, dal momento della creazione, alla preparazione e consegna, con un focus particolare sul tracciamento dello stato e sull'audit delle operazioni.

## Download e Installazione

### Prerequisiti
- Java Development Kit (JDK) 17
- Apache Maven 3.9+
- Git

### Procedura di Download
```bash
# Clonare il repository
git clone https://github.com/NotMikev/awesome-pizza-manager.git

# Accedere alla directory del progetto
cd order.manager
```

## Esecuzione del Progetto

### Configurazione Ambiente di Sviluppo
1. Verificare la versione Java:
   ```bash
   java -version
   ```
   Output atteso: OpenJDK/Java versione 17.x.x

2. Verificare l'installazione Maven:
   ```bash
   mvn -version
   ```
   Output atteso: Apache Maven 3.9.x

### Avvio con IntelliJ IDEA
1. Aprire IntelliJ IDEA
2. Selezionare: `File` → `Open`
3. Selezionare la cartella root del progetto (`order.manager`)
4. Attendere che IntelliJ completi l'importazione Maven (icona in basso a destra)
5. Configurazione Run:
   - Aprire classe `PizzaOrdersManagerApplication`
   - Click destro → `Run PizzaOrdersManagerApplication`
   - Verificare nei log il messaggio: `Started PizzaOrdersManagerApplication in X.XXX seconds`

### Avvio con VS Code
1. Prerequisiti VS Code:
   - Estensione "Extension Pack for Java" (vscjava.vscode-java-pack)
   - Estensione "Spring Boot Extension Pack" (Pivotal.vscode-spring-boot-pack)

2. Aprire il progetto:
   - `File` → `Open Folder`
   - Selezionare la cartella `order.manager`
   - Attendere l'indicizzazione del progetto (icona in basso a destra)

3. Avvio applicazione:
   - Aprire il Command Palette (⇧⌘P su macOS, Ctrl+Shift+P su Windows)
   - Digitare: `Spring Boot Dashboard`
   - Selezionare `order.manager` e cliccare sul tasto play
   - Verificare nei log: `Started PizzaOrdersManagerApplication`

### Avvio Standalone
1. Compilazione:
   ```bash
   # Dalla directory root del progetto
   ./mvnw clean install -DskipTests
   ```
   Verificare output: `BUILD SUCCESS`

2. Verifica del JAR:
   ```bash
   ls -l target/order.manager-0.0.1-SNAPSHOT.jar
   ```
   Dimensione attesa: ~40MB

3. Esecuzione:
   ```bash
   java -jar target/order.manager-0.0.1-SNAPSHOT.jar
   ```
   
4. Verifica avvio corretto:
   - Attendere il log: `Started PizzaOrdersManagerApplication`
   - Nessun errore nel log
   - Endpoint test: http://localhost:8080/awesome/actuator/health
   - Risposta attesa: `{"status":"UP"}`

### Verifica Installazione
1. Swagger UI: http://localhost:8080/awesome/swagger-ui.html
2. H2 Console: http://localhost:8080/awesome/h2-console
3. Health Check: http://localhost:8080/awesome/actuator/health

### Troubleshooting Comuni
- **Errore porta 8080 occupata**:
  ```bash
  lsof -i :8080
  kill -9 [PID]
  ```
  
- **Errore Java version**:
  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  java -version
  ```

- **Errore Maven**:
  ```bash
  ./mvnw -X clean install
  ```
  -X Per log dettagliato

## Architettura e Tecnologie

### Stack Tecnologico
- **Framework**: Spring Boot 3.5.7
- **Linguaggio**: Java 17
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Documentazione API**: OpenAPI/Swagger 2.8.13
- **Logging**: Logback/Slf4j
- **Testing**: JUnit, Spring Test
- **Librerie**: 
  - Lombok 1.18.34
  - MapStruct 1.6.3

### Struttura del Progetto
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

## Flusso Operativo

### Stati dell'Ordine
1. **NEW**: Ordine ricevuto e registrato
2. **IN_PROGRESS**: Ordine in preparazione
3. **READY**: Ordine pronto per la consegna

### Workflow Operativo
1. Cliente effettua un ordine → Stato NEW
2. Sistema assegna un codice univoco
3. Operatore può visualizzare tutti gli ordini in stato NEW
4. Operatore prende in carico → Stato IN_PROGRESS
5. Operatore può visualizzare ordini per ogni stato (NEW, IN_PROGRESS, READY)
6. Completamento preparazione → Stato READY

## API e Documentazione

### Endpoints REST

#### Gestione Ordini
- **POST** `/awesome/api/purchase`
  - Crea nuovo ordine
  - Request body: `{ "pizza": "string" }`
  - Response: PurchaseDto con codice ordine

- **GET** `/awesome/api/purchase/status/{code}`
  - Verifica stato ordine
  - Response: PurchaseDto con stato attuale

- **GET** `/awesome/api/purchase/new`
  - Recupera tutti gli ordini in stato NEW
  - Response: Lista di PurchaseDto con stato NEW

- **GET** `/awesome/api/purchase/status/{status}`
  - Recupera ordini per stato specifico (NEW, IN_PROGRESS, READY)
  - Response: Lista di PurchaseDto dello stato richiesto
  
- **POST** `/awesome/api/purchase/next`
  - Prende in carico il prossimo ordine
  - Response: PurchaseDto dell'ordine assegnato
  
- **POST** `/awesome/api/purchase/next/{code}`
  - Prende in carico un ordine specifico
  - Response: PurchaseDto dell'ordine aggiornato
  
- **POST** `/awesome/api/purchase/{code}/ready`
  - Segna un ordine come pronto
  - Response: PurchaseDto con stato READY

### Documentazione OpenAPI
La documentazione Swagger è disponibile all'endpoint:
```
http://localhost:8080/awesome/swagger-ui.html
```

## Informazioni Aggiuntive

### Versione Corrente
- Versione: 0.0.1-SNAPSHOT
- Stato: In Sviluppo Attivo

### Monitoraggio
Endpoint Actuator disponibili:
- Health Check: `/awesome/actuator/health`

### Database
- Console H2: `/awesome/h2-console`
- URL JDBC: `jdbc:h2:mem:awesome-db`
- Database in-memory (Nel file application.properties presenti info necessarie per accesso)

### Testing
Esecuzione dei test:
```bash
./mvnw test
```

Il progetto include:
- Test di Integrazione API (`ApiAuditIntegrationTest`)
- Test del Flusso Acquisto (`PurchaseFlowTest`)
- Test di Presa in Carico (`TakeNextByCodeTest`)
- Test Applicazione (`PizzaOrdersManagerApplicationTests`)

### Note di Debug
- Logging dettagliato configurato in `logback-spring.xml`
- Log applicativi in `/logs/app.log` (giorno successivo vengono storicizzati in file con data specifica)
- Log specifico pizzeria in `/logs/awesome-pizza.log`
- Log di audit in database H2

### Contribuzione
1. Effettuare fork del repository
2. Creare un branch per le modifiche
3. Implementare le modifiche con relativi test
4. Aprire una Pull Request