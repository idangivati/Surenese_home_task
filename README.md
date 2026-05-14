# Customer Support Hub

A secure REST API backend built with Spring Boot 3.x for managing customer support tickets.

## Tech Stack

- Java 22
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8
- Docker & Docker Compose
- Maven

## Roles

| Role | Permissions |
|---|---|
| ADMIN | Full access to everything |
| AGENT | Create/manage customers, view/update tickets from their customers |
| CUSTOMER | Create/view their own tickets and profile |

## API Endpoints

### Auth
| Method | URL | Description | Access |
|---|---|---|---|
| POST | /auth/login | Login and get JWT token | Public |

### Customers
| Method | URL | Description | Access |
|---|---|---|---|
| POST | /api/customers | Create a new customer | AGENT |
| GET | /api/customers | Get all agent's customers | AGENT |
| GET | /api/customers/paged?page=0&size=10 | Get customers paginated | AGENT |
| PUT | /api/agents/me | Update agent profile | AGENT |
| GET | /api/customers/me | Get own profile | CUSTOMER |
| PUT | /api/customers/me | Update own profile | CUSTOMER |

### Tickets
| Method | URL | Description | Access |
|---|---|---|---|
| POST | /api/tickets | Create a ticket | CUSTOMER |
| GET | /api/tickets | Get own tickets | CUSTOMER |
| GET | /api/tickets/paged?page=0&size=10 | Get own tickets paginated | CUSTOMER |
| GET | /api/tickets/{id} | Get ticket by ID | CUSTOMER |
| GET | /api/tickets/agent | Get all customers' tickets | AGENT |
| GET | /api/tickets/agent/paged?page=0&size=10 | Get customers' tickets paginated | AGENT |
| PATCH | /api/tickets/{id}/status | Update ticket status | AGENT |

## Ticket Statuses
- `OPEN` — default when created
- `IN_PROGRESS` — agent is working on it
- `CLOSED` — resolved

## How to Run

### Option 1 — Docker Compose (Recommended)
```bash
docker-compose up --build
```
App will be available at http://localhost:8080

### Option 2 — Local Development
1. Start MySQL:
```bash
docker run -d --name support-hub-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=support_hub -p 3306:3306 mysql:8
```

2. Run the app:
```bash
mvn spring-boot:run
```

## Default Users (seeded on startup in dev profile)

| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |
| agent1 | agent123 | AGENT |

## Running Tests

```bash
mvn test
```

## Example Usage

### 1. Login
```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"agent1","password":"agent123"}'
```

### 2. Create Customer (as agent)
```bash
curl -X POST http://localhost:8080/api/customers \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <token>" \
-d '{"username":"customer1","password":"customer123","email":"customer1@example.com"}'
```

### 3. Create Ticket (as customer)
```bash
curl -X POST http://localhost:8080/api/tickets \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <token>" \
-d '{"title":"Issue with login","description":"I cannot login to my account"}'
```

### 4. Update Ticket Status (as agent)
```bash
curl -X PATCH http://localhost:8080/api/tickets/1/status \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <token>" \
-d '{"status":"IN_PROGRESS"}'
```

### 5. Get Tickets Paginated (as customer)
```bash
curl -X GET "http://localhost:8080/api/tickets/paged?page=0&size=10" \
-H "Authorization: Bearer <token>"
```

## Security

- Passwords hashed with BCrypt
- JWT tokens expire after 24 hours
- Role-based access control on all endpoints
- Input validation on all requests
- Stateless authentication (no sessions)

## Project Structure

```
src/main/java/com/example/demo/
├── controller/    # REST API endpoints
├── service/       # Business logic
├── repository/    # Database queries
├── model/         # JPA entities
├── dto/           # Request/Response objects
├── security/      # JWT + Spring Security config
└── exception/     # Global error handling
```