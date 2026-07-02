<div align="center">

# 🤖 AI Chatbot SaaS Backend

A backend learning project for a conversational AI application, built with Java, Spring Boot, PostgreSQL, Redis, Docker, and Ollama.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Ollama](https://img.shields.io/badge/Ollama-Llama_3.2-000000?style=for-the-badge&logo=ollama&logoColor=white)

</div>

---

## 📌 About

This is a backend learning project for building a conversational AI application.

I built this project to understand how a backend application is structured and how different technologies work together. It includes user authentication, conversation management, PostgreSQL storage, Redis rate limiting, Docker services, and local AI responses through Ollama.

---

## ✨ Current Features

- 🔐 **JWT Authentication** — User registration and login with protected endpoints.
- 💬 **Conversation Management** — Create, retrieve, and delete conversations through REST APIs.
- 📝 **Persistent Chat History** — Store user messages and AI responses in PostgreSQL.
- 🧠 **Local AI Integration** — Generate responses using Ollama and Llama 3.2.
- ⚡ **Redis Rate Limiting** — Limit each user to 20 messages per minute.
- 🗃️ **Database Migrations** — Manage database schema with Flyway.
- 🐳 **Docker Services** — Run PostgreSQL, Redis, and Ollama with Docker Compose.
- 📊 **Health Endpoint** — Expose health information through Spring Boot Actuator.

---

## 🛠 Technologies Used

| Category | Technology |
| --- | --- |
| Language | Java 21 |
| Backend Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL 16 |
| Cache / Rate Limiting | Redis 7 |
| Authentication | Spring Security and JWT |
| Database Migrations | Flyway |
| AI Model | Ollama with Llama 3.2 |
| HTTP Client | RestTemplate |
| Containerization | Docker and Docker Compose |
| API Testing | Postman |

---

## 🚀 How to Run Locally

### Prerequisites

- Java 21
- Docker Desktop
- Git

### 1. Clone the repository

```bash
git clone https://github.com/riyanikam05/ai-chatbot-saas.git
cd ai-chatbot-saas
```
### 2. Start PostgreSQL, Redis, and Ollama
```bash
docker compose up -d postgres redis ollama
```
Verify that the services are running:
```bash
docker ps
```
You should see containers for PostgreSQL, Redis, and Ollama.

### 3. Download the AI model
```bash
docker exec -it ai-chatbot-saas-ollama-1 ollama pull llama3.2
```
Verify the downloaded model:
```bash
docker exec -it ai-chatbot-saas-ollama-1 ollama list
```
Expected output:
```bash
llama3.2:latest
```
### 4. Configure the backend
```bash
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
```
### 5. Run the backend

For Windows PowerShell:
```bash
.\mvnw.cmd spring-boot:run
```
For macOS/Linux:
```bash
./mvnw spring-boot:run
```
The API runs at:
```bash
http://localhost:8081
```
## 📁 Project Structure
```bash
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
├── compose.yaml
├── Dockerfile
├── pom.xml
└── README.md
```
## 🗄 Database Schema
```bash
users
  id, name, email, password, created_at

conversations
  id, user_id, title, created_at, updated_at

messages
  id, conversation_id, role, content, created_at
```
The schema is managed using Flyway migrations.

## 📡 Main API Endpoints
# Authentication
```bash
Method	Endpoint	Description
POST	/api/auth/register	Register a new user
POST	/api/auth/login	Log in and receive a JWT token
GET	/api/auth/me	Get current user details
Conversations
Method	Endpoint	Description
POST	/api/conversations	Create a new conversation
GET	/api/conversations	Get all conversations for the logged-in user
DELETE	/api/conversations/{id}	Delete a conversation
GET	/api/conversations/{id}/messages	Get messages in a conversation
POST	/api/conversations/{id}/messages	Send a message and receive an AI response
```
Protected endpoints require this header:

Authorization: Bearer <your_jwt_token>

## 🏗 Current Architecture
```bash
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
      │   └── Rate limiting
      │
      └── Ollama
          └── Llama 3.2 local model
```
## 🚧 Next Feature
 Add PDF upload and document question answering using RAG.
## 📚 What I Learned Through This Project
How to structure a Spring Boot backend using controllers, services, repositories, DTOs, and configuration classes.
How to build REST APIs for user authentication, conversations, and chat messages.
How JWT authentication works with Spring Security and protected endpoints.
How to store and retrieve application data using PostgreSQL and Spring Data JPA.
How to manage database changes using Flyway migrations.
How Redis can be used to implement per-user API rate limiting.
How Docker Compose can run PostgreSQL, Redis, and Ollama as separate services.
How to send conversation history from a Java backend to a local LLM through the Ollama REST API.
How to test backend endpoints using Postman.
## 📄 License

This project is for learning and portfolio purposes.

<div align="center">

Built by Riya Nikam as a learning project · 2026

</div>
