<div align="center">

# 🤖 AI Chatbot SaaS Backend

A production-style REST API for an AI chatbot, built with Spring Boot, PostgreSQL, Redis, Docker, and Ollama.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-Llama_3.2-000000?style=for-the-badge&logo=ollama&logoColor=white)

JWT-secured chat APIs with persistent conversation history, Redis rate limiting, and local AI responses through Ollama.

[Features](#-features) · [Tech Stack](#-tech-stack) · [Getting Started](#-getting-started) · [API Reference](#-api-reference) · [Architecture](#-architecture)

</div>

---

## 📌 About the Project

AI Chatbot SaaS Backend is a REST API for a conversational AI application.

It allows users to register, log in, create conversations, send messages, and receive AI-generated responses. User accounts, conversations, and messages are stored in PostgreSQL. Redis enforces per-user rate limits, while Ollama runs the Llama 3.2 model locally.

This project focuses on backend engineering concepts such as authentication, REST API design, database migrations, Docker-based services, and local LLM integration.

---

## ✨ Features

- 🔐 **JWT Authentication** — Register and log in with email and password. Protected endpoints require a valid JWT token.
- 🧠 **Local AI Integration** — Uses Ollama and the Llama 3.2 model locally, with no paid API key required.
- 💬 **Persistent Conversations** — Stores conversations and messages in PostgreSQL.
- 🗂️ **Conversation Management** — Create, retrieve, and delete conversations through REST APIs.
- ⚡ **Redis Rate Limiting** — Limits each user to 20 messages per minute.
- 🗃️ **Database Migrations** — Uses Flyway to version and manage the database schema.
- 🐳 **Docker Compose Setup** — Runs PostgreSQL, Redis, and Ollama as Docker services.
- 📊 **Health Endpoints** — Spring Boot Actuator exposes health and application information.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL 16 |
| Cache / Rate Limiting | Redis 7 |
| Authentication | Spring Security, JWT |
| Database Migrations | Flyway |
| AI Model | Ollama, Llama 3.2 |
| HTTP Client | RestTemplate |
| Containerization | Docker, Docker Compose |
| API Testing | Postman |

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- Docker Desktop
- Git

### 1. Clone the repository

```bash
git clone https://github.com/riyanikam05/ai-chatbot-saas.git
cd ai-chatbot-saas
2. Start PostgreSQL, Redis, and Ollama
docker compose up -d postgres redis ollama

Verify that the services are running:

docker ps

You should see containers for PostgreSQL, Redis, and Ollama.

3. Download the AI model
docker exec -it ai-chatbot-saas-ollama-1 ollama pull llama3.2

Verify the downloaded model:

docker exec -it ai-chatbot-saas-ollama-1 ollama list

Expected output:

llama3.2:latest
4. Configure the backend

Update src/main/resources/application.yml if required:

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydatabase
    username: postgres
    password: your_postgres_password

  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8081

ollama:
  base-url: http://localhost:11434
  model: llama3.2:latest
5. Run the backend

For Windows PowerShell:

.\mvnw.cmd spring-boot:run

For macOS/Linux:

./mvnw spring-boot:run

The API runs at:

http://localhost:8081
📁 Project Structure
ai-chatbot-saas/
├── src/
│   └── main/
│       ├── java/com/riya/aichatbot/
│       │   ├── AiChatbotSaasApplication.java
│       │   ├── ai/
│       │   │   └── OllamaService.java
│       │   ├── auth/
│       │   │   ├── AuthController.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   ├── JwtService.java
│       │   │   ├── User.java
│       │   │   └── UserRepository.java
│       │   ├── chat/
│       │   │   ├── ChatController.java
│       │   │   ├── ChatService.java
│       │   │   ├── Conversation.java
│       │   │   ├── Message.java
│       │   │   ├── ConversationRepository.java
│       │   │   └── MessageRepository.java
│       │   └── config/
│       │       ├── RestTemplateConfig.java
│       │       ├── RedisConfig.java
│       │       └── SecurityConfig.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/
│               ├── V1__create_users.sql
│               ├── V2__create_conversations.sql
│               └── V3__create_messages.sql
├── compose.yaml
├── Dockerfile
├── pom.xml
└── README.md
🗄 Database Schema
users
  id, name, email, password, created_at

conversations
  id, user_id, title, created_at, updated_at

messages
  id, conversation_id, role, content, created_at

The schema is managed using Flyway migrations that run automatically when the application starts.

📡 API Reference
Authentication
Method	Endpoint	Authentication	Description
POST	/api/auth/register	No	Register a new user
POST	/api/auth/login	No	Log in and receive a JWT token
GET	/api/auth/me	Yes	Get the current user's details
Conversations
Method	Endpoint	Authentication	Description
POST	/api/conversations	Yes	Create a conversation
GET	/api/conversations	Yes	Get all conversations for the current user
DELETE	/api/conversations/{id}	Yes	Delete a conversation
GET	/api/conversations/{id}/messages	Yes	Get all messages in a conversation
POST	/api/conversations/{id}/messages	Yes	Send a message and receive an AI response
Send Message Example
{
  "message": "Explain recursion in simple words"
}

Protected endpoints require:

Authorization: Bearer <your_jwt_token>
🏗 Architecture
Client / Postman
      │
      │ HTTP requests with JWT
      ▼
Spring Boot Backend
      │
      ├── PostgreSQL
      │   └── Users, conversations, and messages
      │
      ├── Redis
      │   └── Per-user rate limiting
      │
      └── Ollama REST API
          └── Llama 3.2 local model
Chat Message Flow
A client sends a message to the Spring Boot backend.
Spring Security validates the JWT token.
ChatService verifies that the conversation belongs to the authenticated user.
Redis checks the user's rate limit.
The backend loads recent conversation messages from PostgreSQL.
OllamaService sends the message history to Ollama.
The AI response is saved in PostgreSQL.
The API returns the AI response as JSON.
⚙️ Configuration
Variable	Default Value	Description
JWT_SECRET	Development value	Secret used to sign JWT tokens
OLLAMA_BASE_URL	http://localhost:11434	Ollama API URL
OLLAMA_MODEL	llama3.2:latest	Ollama model name
SPRING_DATASOURCE_URL	jdbc:postgresql://localhost:5432/mydatabase	PostgreSQL connection URL
GOOGLE_CLIENT_ID	placeholder	Google OAuth client ID
GOOGLE_CLIENT_SECRET	placeholder	Google OAuth client secret
🚧 Final Planned Feature
 Add PDF upload and document question answering using RAG.
📚 What I Learned
JWT authentication and Spring Security filter chains
REST API design with Spring Boot
PostgreSQL database design and Flyway migrations
Redis counters for rate limiting
Docker Compose for multi-service applications
Sending conversation context to a local LLM through Ollama
Designing a backend application with layered services and repositories
📄 License

This project is for learning and portfolio purposes.

<div align="center">

Built by Riya Nikam · BTech Project · 2026

</div>