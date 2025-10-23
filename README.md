# Currency Exchange Service

A Spring Boot application that provides currency conversion and fetches the latest exchange rates from the Riksbank API. Exchange rates are stored in an in-memory H2 database and include automatically calculated inverse rates for convenience.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Compile and Build](#compile-and-build)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [H2 Database Console](#h2-database-console)
- [Notes](#notes)
- [Future Improvements](#future-improvements)

## Prerequisites

- Java 17 or newer
- Maven 3.8+
- Internet connection to access the Riksbank API

## Configuration

### `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:currency-db
    driverClassName: org.h2.Driver
    username: user
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update

riksbank:
  api:
    key: "<YOUR_RIKSBANK_API_KEY>"
```

**Important:** Replace `<YOUR_RIKSBANK_API_KEY>` with your actual API key from Riksbank.

**Note:** The database uses an in-memory configuration (`jdbc:h2:mem`), meaning all data will be lost when the application stops. This is ideal for development and testing.

## Compile and Build

1. Open a terminal in the project root directory.

2. Compile the project using Maven:
   ```bash
   mvn clean compile
   ```

3. Package the application into a JAR:
   ```bash
   mvn clean package
   ```

The JAR will be created in the `target/` directory, e.g., `currency-exchange-service-0.0.1-SNAPSHOT.jar`.

## Running the Application

### Option 1: Using Maven

```bash
mvn spring-boot:run
```

### Option 2: Using the JAR

```bash
java -jar target/currency-exchange-0.0.1-SNAPSHOT.jar
```

The application will start on port **8080** by default.

## API Endpoints

### Convert Currency

**POST** `/convert`

**Request body:**
```json
{
  "fromCurrency": "SEK",
  "toCurrency": "USD",
  "amount": 100
}
```

**Response:**
```
100 * exchangeRate
```

### Fetch Latest Exchange Rates

**POST** `/latestExchangeRates`

- Returns all exchange rate combinations including automatically calculated inverse rates.
- If rates for the current bank day exist, it will return them without calling the Riksbank API.

## H2 Database Console

- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:currency-db`
- **Username:** `user`
- **Password:** leave blank (or match your configuration)

This console allows you to inspect and query stored exchange rates in real-time.

## Notes

- The service calculates inverse exchange rates automatically.
- Global exception handling ensures clear error messages for invalid requests or API errors.
- **All exchange rates are stored in-memory and will be lost when the application restarts.**
- To persist data across restarts, change the datasource URL to `jdbc:h2:file:./data/currency-db` in `application.yml`.

## Future Improvements

This project has room for several enhancements:

### Testing
- **Unit Tests:** Add comprehensive unit tests for service layer and business logic
- **Integration Tests:** Implement integration tests for API endpoints
- **Mock External API:** Create tests with mocked Riksbank API responses to ensure reliability

### Features
- **Additional Currencies:** Expand support beyond the current currencies 
- **Historical Data:** Add endpoints to fetch and store historical exchange rates

### Performance & Scalability
- **Rate Limiting:** Add API rate limiting to prevent abuse
- **Scheduled Updates:** Implement automatic rate updates at publishing time
