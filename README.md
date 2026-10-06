# AI Chatbot SaaS

An AI-powered chatbot SaaS backend built with **Java 21 and Spring Boot** that allows users to upload PDF documents and ask questions about their content using a **Retrieval-Augmented Generation (RAG)** pipeline.

The application combines **Ollama embeddings**, **ChromaDB vector search**, and **Groq LLM inference** to generate context-aware answers from the user's uploaded documents.

---

## 🚀 Features

- 🔐 JWT-based authentication
- 👤 User registration, login, and profile APIs
- 🔒 BCrypt password hashing
- 📄 PDF document upload
- 🔎 PDF text extraction using Apache PDFBox
- ✂️ Text chunking for document processing
- 🧠 Local embeddings using Ollama
- 🗄️ Vector storage and similarity search using ChromaDB
- 🤖 LLM-powered answers using Groq
- 💬 Conversation and message history
- 🔒 User-level document and conversation isolation
- 🐳 Dockerized PostgreSQL, ChromaDB, and Ollama
- 🗃️ Database migrations using Flyway
- 📖 Swagger / OpenAPI documentation
- ❤️ Spring Boot Actuator health endpoints
- ⚠️ Centralized exception handling
- 🧪 Unit and integration testing support

---

## 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │   Postman / API     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    Spring Boot      │
                         │       REST API      │
                         └──────────┬──────────┘
                                    │
                  ┌─────────────────┼─────────────────┐
                  │                 │                 │
                  ▼                 ▼                 ▼
          ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
          │ PostgreSQL   │  │ Spring       │  │ JWT          │
          │              │  │ Security     │  │ Authentication│
          └──────────────┘  └──────────────┘  └──────────────┘
                                    │
                                    │
                         Document Processing
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      PDFBox         │
                         │  PDF Text Extract   │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Text Chunking     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │      Ollama         │
                         │     Embeddings      │
                         │  nomic-embed-text   │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │     ChromaDB        │
                         │   Vector Storage    │
                         └──────────┬──────────┘
                                    │
                             Semantic Search
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │       Groq          │
                         │    LLM Inference    │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   Generated Answer  │
                         └─────────────────────┘
```

---

## 🧠 RAG Pipeline

The core of the application is a **Retrieval-Augmented Generation (RAG)** pipeline.

```text
PDF Upload
    │
    ▼
Extract Text
    │
    ▼
Split Into Chunks
    │
    ▼
Generate Embeddings
    │
    ▼
Store Vectors in ChromaDB
    │
    ▼
User Asks Question
    │
    ▼
Generate Question Embedding
    │
    ▼
Semantic Similarity Search
    │
    ▼
Retrieve Relevant Chunks
    │
    ▼
Build Context
    │
    ▼
Send Context + Question to Groq
    │
    ▼
Generate Answer
    │
    ▼
Save Conversation + Message
    │
    ▼
Return Response
```

---

## 🔍 How RAG Works

### 1. Document Upload

An authenticated user uploads a PDF through:

```http
POST /api/v1/documents/upload
```

The application:

1. Validates the uploaded file.
2. Generates a unique stored filename.
3. Saves the PDF locally.
4. Stores document metadata in PostgreSQL.
5. Extracts text using Apache PDFBox.
6. Splits the extracted text into chunks.
7. Generates embeddings using Ollama.
8. Stores the embeddings and metadata in ChromaDB.

### 2. User Question

The user sends a question through:

```http
POST /api/chat/ask
```

The question is converted into an embedding using Ollama.

### 3. Semantic Search

The question embedding is sent to ChromaDB.

ChromaDB performs a similarity search and retrieves the most relevant document chunks.

The application currently uses:

```text
Top K = 3
```

### 4. Context Construction

The retrieved chunks are combined into a context that is passed to the LLM.

The prompt instructs the model to answer using the retrieved document context.

If no relevant chunks are found, the API returns:

```text
I couldn't find any relevant information in your uploaded documents.
```

### 5. LLM Generation

The retrieved context and user's question are sent to Groq for answer generation.

The generated answer is then saved as an assistant message and returned to the client.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Build Tool | Maven |
| Security | Spring Security |
| Authentication | JWT |
| Password Hashing | BCrypt |
| Database | PostgreSQL 16 |
| Database Migration | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Vector Database | ChromaDB |
| Embeddings | Ollama + `nomic-embed-text` |
| LLM | Groq |
| PDF Processing | Apache PDFBox |
| API Documentation | SpringDoc OpenAPI / Swagger |
| Monitoring | Spring Boot Actuator |
| Containerization | Docker / Docker Compose |
| Testing | JUnit, Spring Boot Test, Mockito, WireMock |

---

## 📁 Project Structure

```text
ai-chatbot-saas/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__initial_schema.sql
│   │       │
│   │       └── application.yml
│   │
│   └── test/
│
├── uploads/
├── compose.yaml
├── pom.xml
├── .env
├── .gitignore
└── README.md
```

---

## 🔐 Authentication

Authentication is implemented using **Spring Security + JWT**.

Passwords are hashed using **BCrypt** before being stored in PostgreSQL.

JWT tokens are provided using:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive JWT |
| GET | `/api/auth/me` | Get current user |

### Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "name": "Riya",
  "email": "riya@example.com",
  "password": "your-password"
}
```

### Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "riya@example.com",
  "password": "your-password"
}
```

### Get Current User

```http
GET /api/auth/me
```

Requires:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 📄 Document APIs

Base URL:

```text
/api/v1/documents
```

### Upload PDF

```http
POST /api/v1/documents/upload
```

Content type:

```http
multipart/form-data
```

Form field:

```text
file=<PDF_FILE>
```

The maximum configured upload size is:

```text
10 MB
```

### Processing Pipeline

```text
PDF
 ↓
PDFBox
 ↓
Extracted Text
 ↓
Text Chunks
 ↓
Ollama Embeddings
 ↓
ChromaDB
```

### List Documents

```http
GET /api/v1/documents
```

Requires authentication.

Returns documents belonging to the authenticated user.

---

## 💬 Chat APIs

Base URL:

```text
/api/chat
```

### Ask a Question

```http
POST /api/chat/ask
```

Requires authentication.

Example request:

```json
{
  "question": "What is the main topic of the document?",
  "conversationId": 1
}
```

`conversationId` is optional.

If it is omitted, a new conversation is created.

### Example Response

```json
{
  "conversationId": 1,
  "question": "What is the main topic of the document?",
  "answer": "The document primarily discusses...",
  "retrievedChunks": [
    "Relevant document content...",
    "Another relevant section...",
    "Additional supporting information..."
  ]
}
```

---

## 🗨️ Conversation APIs

Base URL:

```text
/api/conversations
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/conversations` | Create a conversation |
| GET | `/api/conversations` | List user's conversations |
| GET | `/api/conversations/{id}` | Get a conversation |
| GET | `/api/conversations/{id}/messages` | Get conversation messages |
| DELETE | `/api/conversations/{id}` | Delete a conversation |

All conversation endpoints require authentication.

Users can only access their own conversations.

---

## 🗃️ Database Design

The application uses PostgreSQL for persistent application data.

### Users

```text
users
├── id
├── name
├── email
├── password
└── created_at
```

The email field is unique.

### Conversations

```text
conversations
├── id
├── user_id
├── title
├── created_at
└── updated_at
```

Relationship:

```text
User
  │
  └── 1 : N
       │
       ▼
  Conversations
```

### Messages

```text
messages
├── id
├── conversation_id
├── role
├── content
└── timestamp
```

Relationship:

```text
Conversation
     │
     └── 1 : N
          │
          ▼
       Messages
```

### Documents

```text
documents
├── id
├── user_id
├── original_filename
├── stored_filename
├── file_path
├── file_size
├── mime_type
├── status
├── error_message
├── chunk_count
└── uploaded_at
```

Documents belong to individual users.

---

## 🐳 Docker Infrastructure

The project uses Docker Compose for local infrastructure.

| Service | Image | Port |
|---|---|---|
| PostgreSQL | `postgres:16-alpine` | `5434 → 5432` |
| ChromaDB | `chromadb/chroma:latest` | `8000` |
| Ollama | `ollama/ollama:latest` | `11434` |

### Start Infrastructure

```bash
docker compose up -d
```

Check running containers:

```bash
docker compose ps
```

### Stop Infrastructure

```bash
docker compose down
```

### View Logs

```bash
docker compose logs -f
```

For a specific service:

```bash
docker compose logs -f postgres
```

```bash
docker compose logs -f chromadb
```

```bash
docker compose logs -f ollama
```

---

## 🧠 Ollama Setup

The application uses:

```text
nomic-embed-text
```

for document and question embeddings.

Pull the embedding model into the Ollama container:

```bash
docker exec chatbot-ollama ollama pull nomic-embed-text
```

Verify installed models:

```bash
docker exec chatbot-ollama ollama list
```

The application is configured to use:

```yaml
ollama:
  base-url: http://localhost:11434
  model: llama3.2
  embedding-model: nomic-embed-text
```

---

## ⚙️ Configuration

Create a `.env` file in the project root.

Example:

```env
POSTGRES_DB=mydatabase
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

JWT_SECRET=your_base64_encoded_jwt_secret

GROQ_API_KEY=your_groq_api_key
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=openai/gpt-oss-20b

OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2
OLLAMA_EMBEDDING_MODEL=nomic-embed-text

CHROMA_BASE_URL=http://localhost:8000
CHROMA_TENANT=default_tenant
CHROMA_DATABASE=default_database
CHROMA_COLLECTION=documents
```

> **Never commit `.env` or API keys to GitHub.**

---

## 🔑 JWT Configuration

JWT configuration is provided through:

```env
JWT_SECRET=your_base64_encoded_jwt_secret
```

The application Base64-decodes the secret and uses it for HS256 JWT signing.

JWT expiration is configured for:

```text
86400000 ms
```

which corresponds to:

```text
24 hours
```

---

## 🗄️ PostgreSQL Configuration

The application connects to PostgreSQL using:

```text
jdbc:postgresql://127.0.0.1:5434/${POSTGRES_DB:mydatabase}
```

Default configuration:

```text
Host:     127.0.0.1
Port:     5434
Database: mydatabase
User:     postgres
```

The Docker container internally exposes PostgreSQL on:

```text
5432
```

while the host uses:

```text
5434
```

---

## 🧬 Database Migrations

Flyway is used for database schema management.

Migrations are located under:

```text
src/main/resources/db/migration/
```

Flyway configuration:

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  out-of-order: false
  validate-on-migrate: true
```

On application startup, Flyway validates and applies pending migrations.

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/riyanikam05/ai-chatbot-saas.git
cd ai-chatbot-saas
```

### 2. Configure Environment Variables

Create:

```text
.env
```

Add your credentials and service configuration:

```env
POSTGRES_DB=mydatabase
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password

JWT_SECRET=your_base64_encoded_jwt_secret

GROQ_API_KEY=your_groq_api_key
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=openai/gpt-oss-20b

OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2
OLLAMA_EMBEDDING_MODEL=nomic-embed-text

CHROMA_BASE_URL=http://localhost:8000
CHROMA_TENANT=default_tenant
CHROMA_DATABASE=default_database
CHROMA_COLLECTION=documents
```

### 3. Start Docker Services

```bash
docker compose up -d
```

Verify:

```bash
docker compose ps
```

### 4. Pull the Ollama Embedding Model

```bash
docker exec chatbot-ollama ollama pull nomic-embed-text
```

### 5. Build the Application

#### Windows

```powershell
.\mvnw.cmd clean package
```

#### Linux / macOS

```bash
./mvnw clean package
```

### 6. Run the Application

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

---

## 📖 Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8081/v3/api-docs
```

Swagger provides interactive documentation for the API.

JWT bearer authentication is configured in the OpenAPI configuration.

---

## ❤️ Health Monitoring

Spring Boot Actuator exposes:

```text
/actuator/health
/actuator/info
/actuator/mappings
```

Health endpoint:

```text
http://localhost:8081/actuator/health
```

---

## 🔒 Security

The application implements:

- Spring Security
- Stateless authentication
- JWT authorization
- BCrypt password hashing
- Protected API endpoints
- User ownership checks
- Input validation
- Centralized exception handling

### Public Endpoints

The following endpoints do not require authentication:

```text
POST /api/auth/register
POST /api/auth/login

/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**

/actuator/**
```

All other endpoints require authentication.

---

## 👤 User Data Isolation

The application ensures users can only access their own resources.

For example:

```text
User A
 ├── Documents
 ├── Conversations
 └── Messages

User B
 ├── Documents
 ├── Conversations
 └── Messages
```

User A cannot access User B's conversations or documents.

Conversation ownership is validated before accessing or deleting conversations.

---

## ⚠️ Error Handling

The application uses a centralized:

```text
GlobalExceptionHandler
```

API errors are returned using a structured format.

Example:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request",
  "path": "/api/chat/ask"
}
```

Custom exceptions include:

```text
ChromaException
DocumentProcessingException
EmailAlreadyExistsException
EmbeddingGenerationException
GroqException
PdfExtractionException
```

---

## 🧪 Testing

The project includes testing dependencies for:

- Spring Boot testing
- Spring Security testing
- JUnit
- Mockito
- WireMock

Run tests with:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

---

## 🧩 Key Components

### Authentication

```text
AuthController
AuthService
User
UserRepository
JwtService
JwtAuthFilter
SecurityConfig
```

### Chat

```text
ChatController
ChatService
ChatRequest
ChatResponse
ConversationService
Message
```

### Documents

```text
DocumentController
DocumentService
PdfExtractorService
TextChunkingService
Document
```

### Vector Search

```text
ChromaDB
Ollama Embeddings
Semantic Similarity Search
Document Metadata Filtering
```

### LLM

```text
Groq API
```

The LLM receives the retrieved document context and user question.

---

## 📐 RAG Configuration

The application uses the following RAG-related configuration:

| Configuration | Value |
|---|---|
| Top K | `3` |
| Temperature | `0.5` |
| Max Tokens | `150` |
| Embedding Model | `nomic-embed-text` |
| LLM | Groq |

These defaults are represented in:

```text
AppConstants
```

---

## 🔄 Complete User Flow

```text
1. User registers
        │
        ▼
2. User logs in
        │
        ▼
3. JWT token generated
        │
        ▼
4. User uploads PDF
        │
        ▼
5. PDF text extracted
        │
        ▼
6. Text divided into chunks
        │
        ▼
7. Embeddings generated
        │
        ▼
8. Embeddings stored in ChromaDB
        │
        ▼
9. User asks a question
        │
        ▼
10. Question embedding generated
        │
        ▼
11. ChromaDB performs semantic search
        │
        ▼
12. Relevant chunks retrieved
        │
        ▼
13. Context sent to Groq
        │
        ▼
14. Answer generated
        │
        ▼
15. Conversation and messages saved
        │
        ▼
16. Answer returned to user
```

---

## 📐 Design Decisions

### PostgreSQL

PostgreSQL is used for structured application data such as:

- Users
- Conversations
- Messages
- Documents
- Document metadata

### ChromaDB

ChromaDB is used for vector storage and semantic similarity search.

This separates relational application data from vector search data.

### Ollama

Ollama provides local embedding generation.

This allows document embeddings to be generated locally without relying on an external embedding service.

### Groq

Groq is used for LLM inference after relevant document context has been retrieved.

### Flyway

Flyway provides version-controlled database migrations instead of relying on automatic schema generation.

### JWT

JWT provides stateless authentication for the REST API.

---

## 📦 Docker Volumes

Docker volumes are used to persist service data:

```text
postgres-data
chroma-data
ollama-data
```

This allows container data to survive container restarts.

---

## 🛡️ Security Considerations

Before deploying this application publicly:

- Use strong PostgreSQL credentials.
- Use a strong randomly generated JWT secret.
- Never commit `.env`.
- Never expose API keys in source code.
- Use HTTPS in production.
- Configure CORS appropriately when adding a frontend.
- Configure production database credentials separately.
- Store uploaded files securely.
- Add file type validation and malware scanning for production use.
- Configure appropriate rate limiting.
- Restrict actuator endpoints in production.
- Avoid exposing sensitive exception details.

---

## 📊 Project Highlights

This project demonstrates practical experience with:

```text
Java
Spring Boot
Spring Security
JWT Authentication
REST APIs
Spring Data JPA
PostgreSQL
Flyway
Docker
Docker Compose
Ollama
Embeddings
Vector Databases
ChromaDB
RAG
LLM Integration
Groq API
PDF Processing
Apache PDFBox
Exception Handling
OpenAPI / Swagger
Actuator
Testing
```

---

## 💡 What This Project Demonstrates

The project combines traditional backend engineering with modern AI application development.

It demonstrates how to build a backend system that combines:

```text
Secure REST APIs
        +
Relational Database
        +
Document Processing
        +
Vector Search
        +
Embeddings
        +
LLM Inference
        +
RAG
        +
Dockerized Infrastructure
```

The result is a backend capable of turning a collection of user-uploaded PDF documents into an interactive question-answering system.

---

## 👩‍💻 Author

**Riya Nikam**

GitHub:

https://github.com/riyanikam05

Project:

https://github.com/riyanikam05/ai-chatbot-saas

---

## ⭐ Summary

**AI Chatbot SaaS** is a Java/Spring Boot backend that implements an end-to-end Retrieval-Augmented Generation workflow.

```text
PDF
 ↓
Text Extraction
 ↓
Chunking
 ↓
Embeddings
 ↓
ChromaDB
 ↓
Semantic Retrieval
 ↓
Groq LLM
 ↓
Context-Aware Answer
```

It combines backend engineering, authentication, databases, document processing, vector search, embeddings, LLM integration, RAG, and Dockerized infrastructure into a single application.
