# 🛡️ Resilient Decision Engine

A high-performance, configurable workflow platform built to handle real-world business decisions with strict state management, idempotency, and fault tolerance.

## 🚀 Key Features

* **Configurable Rule Engine:** Workflows (Loan Approval, Onboarding, etc.) are defined in JSON. Change business logic without a single line of code.
* **Idempotency Shield:** Powered by **Redis**. Prevents duplicate processing of the same request ID (HTTP 409 Conflict), ensuring data integrity.
* **State Machine Lifecycle:** Tracks every request through `PENDING`, `EVALUATING`, `APPROVED`, or `REJECTED` states.
* **Engineering Robustness:** Features a 3-tier retry mechanism for external dependency failures (simulated Credit Bureau API).
* **Full Auditability:** Every decision is backed by a granular audit trail in **PostgreSQL**, explaining exactly *why* a rule passed or failed.

---

## 🛠️ Tech Stack

* **Language:** Java
* **Framework:** Spring Boot (version 3.x)
* **Database:** PostgreSQL (Persistence & Auditing)
* **Cache:** Redis (Idempotency Management)
* **Build Tool:** Maven
* **Infrastructure:** Docker & Docker Compose

---

## 🏃‍♂️ Quick Start

### 1. Launch Infrastructure
Ensure Docker Desktop is running, then start the database and cache:
```bash
docker-compose up -d
``` 
## 2. Start the Application
```Bash
./mvnw spring-boot:run
```
The application will start on http://localhost:8080

---
## 🧪 Testing the Core Capabilities
### 1. Execute a Workflow (Happy Path)
Command:

```Bash
curl -i -X POST http://localhost:8080/api/v1/workflows/execute \
  -H "Content-Type: application/json" \
  -H "X-Idempotency-Key: unique-key-101" \
  -d '{
    "workflowId": "loan_approval",
    "payload": {
      "monthlyIncome": "5000",
      "creditScore": "720"
    }
  }'
  ```
Expected Result: 200 OK with finalStatus: APPROVED.

### 2. Test Idempotency (The "Shield")
Run the exact same command again.
Expected Result: 409 Conflict. This proves the Redis-backed idempotency filter is blocking duplicate work.

### 3. Test Business Logic (Rejection & Explainability)
Send a request with low values (e.g., monthlyIncome: 1000).
Expected Result: ```200 OK``` with ```finalStatus: REJECTED```.
Audit Check: View the ```executionTrace``` in the response to see the granular reasoning for the failure.

### 4. Automated Suite
Run the full integration test suite to verify system stability:

```Bash
./mvnw test
```  
---
## 📐 Architecture & Design Decisions
### 1. Separation of Concerns
Domain Layer: Stateless logic for rule evaluation.

Service Layer: Orchestrates state transitions and external API retries.

Filter Layer: Interoperable layer for high-speed idempotency checks before hitting business logic.

### 2. Scaling Considerations
Horizontal Scaling: The engine is stateless; multiple instances can run behind a load balancer.

Distributed Locking: Redis ensures that idempotency is maintained even across a cluster of servers.

### 3. Resilience Strategy
The system implements a retry loop for the ```ExternalDataService```. If the simulated dependency fails, the orchestrator attempts recovery 3 times before transitioning the workflow to a failed state, ensuring the system tolerates intermittent network issues.

---
## 📂 Project Structure
```bash
src/main/java/com/hackathon/decisionengine/
├── controller/   # REST API Endpoints
├── service/      # Orchestration, Retries, & Explanations
├── domain/       # Core Rule Engine & State Logic
├── dto/          # Data Transfer Objects
├── filter/       # Redis-backed Idempotency Interceptors (Security Layer)
├── model/        # Database Entities (AuditLog, WorkflowState)
└── repository/   # Database Access (JPA)
```