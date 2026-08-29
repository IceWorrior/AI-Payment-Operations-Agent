# AI Payment Operations Agent

An AI-powered payment operations backend built with **Java, PostgreSQL, and Ollama**.

The system exposes a REST API for payment operations and uses a local LLM to understand natural-language questions, select the appropriate payment tool, execute it against the payment database, and generate a concise answer.

The entire AI pipeline runs locally through Ollama.

---

## Features

* Payment CRUD operations
* Payment filtering
* Payment statistics
* Payment-method statistics
* Payment risk analysis
* Natural-language payment queries
* Local AI inference using Ollama
* AI tool selection and execution
* Follow-up questions using conversation history
* Tool-call validation
* REST API
* PostgreSQL persistence
* Maven build system
* No external AI API required

---

## Architecture

```text
                    User
                      |
                      v
              REST API /api/ai
                      |
                      v
               PaymentAgent
                      |
                      v
                Ollama / Qwen
                      |
              Tool selection
                      |
                      v
               ToolExecutor
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
   get_payments   filter_payments   statistics
          |           |           |
          +-----------+-----------+
                      |
                      v
               PaymentService
                      |
                      v
              PaymentRepository
                      |
                      v
                 PostgreSQL
```

The AI does **not** directly access the database.

Instead, the AI produces a structured tool call such as:

```json
{
  "tool": "filter_payments",
  "status": "FAILED",
  "paymentMethod": "UPI",
  "minAmount": 5000,
  "maxAmount": null
}
```

The Java application validates this tool call and executes the corresponding Java method.

The resulting payment data is then given back to the LLM so it can generate a human-readable response.

---

# Requirements

Before running the project on another computer, install:

### 1. Java

Java 17 or newer is recommended.

Check:

```bash
java -version
```

### 2. Maven

Check:

```bash
mvn -version
```

### 3. PostgreSQL

Check:

```bash
psql --version
```

### 4. Ollama

Install Ollama from the official website:

https://ollama.com

Check:

```bash
ollama --version
```

---

# Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd AI-Payment-Operations-Agent
```

---

# PostgreSQL Setup

The application expects a PostgreSQL database named:

```text
payment_agent
```

Create the database:

```bash
sudo -u postgres psql
```

Inside PostgreSQL:

```sql
CREATE DATABASE payment_agent;
CREATE USER payment_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE payment_agent TO payment_user;
```

Then exit:

```sql
\q
```

> The exact PostgreSQL commands may vary depending on the operating system and PostgreSQL configuration.

---

# Database Configuration

The application reads database configuration from environment variables.

Set:

```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/payment_agent"
export DATABASE_USER="payment_user"
export DATABASE_PASSWORD="your_password"
```

For Windows PowerShell:

```powershell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/payment_agent"
$env:DATABASE_USER="payment_user"
$env:DATABASE_PASSWORD="your_password"
```

### Important

Do **not** commit database passwords or other credentials to Git.

The project already ignores:

```text
.env
.env.*
```

---

# Ollama Setup

Start Ollama:

```bash
ollama serve
```

If Ollama is already running as a system service, you do not need to run `ollama serve` manually.

Pull the model used by the project:

```bash
ollama pull qwen3.5:9b
```

Verify:

```bash
ollama list
```

You should see:

```text
qwen3.5:9b
```

The application communicates with:

```text
http://localhost:11434
```

---

# Build the Project

From the project directory:

```bash
mvn clean package
```

The compiled JAR will be generated inside:

```text
target/
```

The `target/` directory is intentionally ignored by Git.

---

# Run the Application

Run the generated JAR:

```bash
java -jar target/payment-agent-1.0.jar
```

The HTTP server starts on:

```text
http://localhost:8000
```

---

# API Endpoints

## Get All Payments

```http
GET /api/payments
```

Example:

```bash
curl http://localhost:8000/api/payments
```

Example response:

```json
[
  {
    "id": "pay_001",
    "amount": 2500.0,
    "currency": "INR",
    "status": "SUCCESS",
    "paymentMethod": "UPI"
  }
]
```

---

## Get Payment by ID

```http
GET /api/payments/{id}
```

Example:

```bash
curl http://localhost:8000/api/payments/pay_001
```

---

## Filter Payments

```http
GET /api/payments/filter
```

Supported filters:

```text
status
paymentMethod
minAmount
maxAmount
```

Example:

```bash
curl "http://localhost:8000/api/payments/filter?status=FAILED&paymentMethod=UPI&minAmount=5000"
```

---

## Payment Statistics

```http
GET /api/payments/stats
```

Example:

```bash
curl http://localhost:8000/api/payments/stats
```

Example:

```json
{
  "totalPayments": 8,
  "successfulPayments": 4,
  "failedPayments": 4,
  "pendingPayments": 0,
  "totalAmount": 58000.0,
  "failedAmount": 34500.0,
  "failureRate": 50.0
}
```

---

## Payment Risk Analysis

```http
GET /api/payments/risk
```

Example:

```bash
curl http://localhost:8000/api/payments/risk
```

Example response:

```json
{
  "riskScore": 65,
  "riskLevel": "HIGH",
  "reasons": [
    "Payment failure rate is above 30%",
    "More than Rupees 10,000 is associated with failed payments",
    "Multiple payment failures detected"
  ]
}
```

---

# AI API

The main feature of the project is the AI endpoint:

```http
POST /api/ai
```

Request:

```json
{
  "question": "Show me failed UPI payments above 5000"
}
```

Example:

```bash
curl -X POST http://localhost:8000/api/ai \
  -H "Content-Type: application/json" \
  -d '{"question":"Show me failed UPI payments above 5000"}'
```

The AI can understand natural-language requests such as:

```text
Show me all payments
```

```text
Show me failed UPI payments
```

```text
Show me failed UPI payments above 5000
```

```text
Which payment method has the most failed payments?
```

```text
Give me the payment statistics
```

```text
Is my payment system risky?
```

---

# Follow-up Questions

The agent maintains conversation context.

For example:

### First request

```text
Show me failed UPI payments
```

The agent may return:

```text
There are 2 failed UPI payments:
pay_002 for 7500 INR
pay_005 for 9000 INR
```

### Follow-up

```text
Only those above 8000
```

The agent understands that "those" refers to the previous failed UPI payments.

It then applies the additional amount filter.

This is implemented through the `conversationHistory` maintained by `PaymentAgent`.

---

# How the AI Works

The AI system follows a two-stage process.

## Stage 1 — Tool Selection

The user's question is sent to Ollama.

For example:

```text
Show me failed UPI payments above 5000
```

The model is instructed to return structured JSON:

```json
{
  "tool": "filter_payments",
  "status": "FAILED",
  "paymentMethod": "UPI",
  "minAmount": 5000,
  "maxAmount": null
}
```

Java deserializes this into a `ToolCall` object.

---

## Stage 2 — Validation

`ToolCallValidator` checks the generated tool call.

It verifies things such as:

* Tool name exists
* Tool is supported
* Payment status is valid
* Amount values are valid
* Minimum amount is not greater than maximum amount

Invalid tool calls are rejected before execution.

---

## Stage 3 — Tool Execution

`ToolExecutor` maps the selected tool to a Java method.

For example:

```text
filter_payments
        |
        v
PaymentTools.filterPayments()
        |
        v
PaymentService.filterPayments()
        |
        v
PaymentRepository
        |
        v
PostgreSQL
```

The AI never directly executes SQL.

---

## Stage 4 — Data Grounding

The actual database result is serialized to JSON.

For example:

```json
[
  {
    "id": "pay_002",
    "amount": 7500.0,
    "currency": "INR",
    "status": "FAILED",
    "paymentMethod": "UPI"
  },
  {
    "id": "pay_005",
    "amount": 9000.0,
    "currency": "INR",
    "status": "FAILED",
    "paymentMethod": "UPI"
  }
]
```

This data is then provided to Ollama.

The model is instructed to use the supplied payment data as the authoritative source.

This prevents the AI from inventing payment records.

---

# Project Structure

```text
src/main/java/com/paymentagent/
│
├── ai/
│   ├── AIRequest.java
│   ├── AIResponse.java
│   ├── OllamaClient.java
│   ├── PaymentAgent.java
│   ├── PaymentTools.java
│   ├── ToolCall.java
│   ├── ToolCallValidator.java
│   ├── ToolDefinition.java
│   ├── ToolExecutor.java
│   └── ToolRegistry.java
│
├── controller/
│   └── PaymentController.java
│
├── database/
│   └── Database.java
│
├── model/
│   ├── Payment.java
│   ├── PaymentMethodStats.java
│   ├── PaymentRequest.java
│   ├── PaymentStats.java
│   └── RiskAnalysis.java
│
├── repository/
│   └── PaymentRepository.java
│
├── server/
│   ├── HttpServer.java
│   └── Router.java
│
├── service/
│   ├── PaymentService.java
│   └── RiskService.java
│
├── util/
│   └── JsonUtil.java
│
└── validation/
    └── PaymentValidator.java
```

---

# Important Classes

### `PaymentAgent`

The main AI orchestration layer.

Responsible for:

* Sending prompts to Ollama
* Parsing tool calls
* Maintaining conversation history
* Executing tools
* Sending database results back to the model
* Generating the final response

### `OllamaClient`

Handles HTTP communication with the local Ollama server.

### `ToolCall`

Represents the structured tool request generated by the LLM.

### `ToolCallValidator`

Validates AI-generated tool calls before execution.

### `ToolExecutor`

Routes the selected AI tool to the correct Java implementation.

### `PaymentTools`

Exposes payment operations to the AI layer.

### `PaymentService`

Contains application-level payment logic.

### `PaymentRepository`

Handles database operations.

### `RiskService`

Calculates payment-system risk based on:

* Failure rate
* Failed payment amount
* Number of failed payments

### `Router`

Defines and handles the REST API routes.

---

# Security Design

The application follows a simple separation between the AI and the database.

```text
LLM
 |
 | structured tool call
 v
Validator
 |
 v
Java Tool
 |
 v
Service
 |
 v
Repository
 |
 v
Database
```

The LLM does not receive database credentials and does not directly execute SQL.

Database credentials are supplied through environment variables.

---

# Troubleshooting

## Ollama connection error

Check whether Ollama is running:

```bash
curl http://localhost:11434/api/tags
```

If it is not running:

```bash
ollama serve
```

---

## Model not found

Run:

```bash
ollama list
```

If `qwen3.5:9b` is missing:

```bash
ollama pull qwen3.5:9b
```

---

## PostgreSQL connection error

Check:

```bash
echo "$DATABASE_URL"
echo "$DATABASE_USER"
```

Make sure the password is set:

```bash
echo "$DATABASE_PASSWORD"
```

Also verify PostgreSQL is running.

On Linux systems using systemd:

```bash
sudo systemctl status postgresql
```

---

## Port 8000 already in use

Find the process:

```bash
lsof -i :8000
```

or:

```bash
ss -ltnp | grep 8000
```

Stop the conflicting process or change the application's server port.

---

## Port 11434 already in use

This usually means Ollama is already running.

Check:

```bash
ss -ltnp | grep 11434
```

You normally **do not need to run `ollama serve` again** if Ollama is already running.

---

# Development

Build:

```bash
mvn clean package
```

Run:

```bash
java -jar target/payment-agent-1.0.jar
```

The project intentionally does not commit generated files such as:

```text
target/
out/
*.class
```

These are recreated during compilation.

---

# Example Workflow

A complete request looks like this:

```text
User
 |
 | "Show me failed UPI payments above 5000"
 v
/api/ai
 |
 v
PaymentAgent
 |
 v
Ollama / Qwen
 |
 | filter_payments
 | FAILED
 | UPI
 | minAmount = 5000
 v
ToolCallValidator
 |
 v
ToolExecutor
 |
 v
PaymentTools
 |
 v
PaymentService
 |
 v
PaymentRepository
 |
 v
PostgreSQL
 |
 | payment records
 v
PaymentAgent
 |
 v
Ollama / Qwen
 |
 v
Human-readable response
```

Example final response:

```text
Found 2 failed UPI payments above 5000 INR:
pay_002 with 7500 INR
pay_005 with 9000 INR

Total amount: 16500 INR
```

---

# Technology Stack

| Technology  | Purpose                            |
| ----------- | ---------------------------------- |
| Java        | Backend application                |
| PostgreSQL  | Payment data persistence           |
| JDBC        | Database connectivity              |
| Ollama      | Local LLM runtime                  |
| Qwen 3.5 9B | Natural-language reasoning         |
| Jackson     | JSON serialization/deserialization |
| Maven       | Build and dependency management    |
| HTTP Server | REST API                           |

---

# Design Goals

The project was designed around several principles:

### Local AI

AI inference runs locally using Ollama instead of sending payment data to an external AI API.

### Controlled Tool Execution

The LLM selects tools, but Java controls what can actually be executed.

### Grounded Responses

Final responses are generated using the actual database results rather than allowing the model to invent payment information.

### Separation of Responsibilities

The application separates:

```text
AI
API
Controller
Service
Repository
Database
```

This makes the project easier to maintain and extend.

---

# Future Improvements

Potential future improvements include:

* Authentication and authorization
* JWT-based API security
* Transaction monitoring
* Real-time payment alerts
* More advanced fraud detection
* Payment anomaly detection
* Additional AI tools
* Streaming AI responses
* Docker deployment
* Production database configuration
* Automated tests
* API documentation with OpenAPI/Swagger
* Web dashboard for payment operations

---

# License

Add your preferred license here if you decide to open-source the project.
