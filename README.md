# AI Chatbot SaaS

An AI-powered chatbot SaaS backend built with Java 21 and Spring Boot that allows authenticated users to upload PDF documents and ask questions about their content using a Retrieval-Augmented Generation (RAG) pipeline.

The application combines Google's Gemini Embedding API, PostgreSQL with pgvector for vector storage and semantic search, and Groq LLM inference to generate context-aware answers from uploaded documents.

## 🚀 Features

- 🔐 JWT-based authentication
- 👤 User registration, login, and profile APIs
- 🔒 BCrypt password hashing
- 📄 PDF document upload and management
- 🔎 PDF text extraction using Apache PDFBox
- ✂️ Text chunking for document processing
- 🧠 Embedding generation using Google Gemini
- 🗄️ Vector storage and similarity search using PostgreSQL with pgvector
- 🤖 LLM-powered answers using Groq
- 💬 Conversation and message history
- 🔒 User-level document and conversation isolation
- 🐳 Dockerized PostgreSQL infrastructure
- 🗃️ Database migrations using Flyway
- 📖 Swagger / OpenAPI documentation
- ⚠️ Centralized exception handling
- 🧪 Unit and integration testing support
- ☁️ Cloud deployment using Render and Neon PostgreSQL

## 🏗️ Architecture

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │   Postman / Swagger │
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
                         │   Gemini API        │
                         │     Embeddings      │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    PostgreSQL       │
                         │      pgvector       │
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

## 🧠 RAG Pipeline

The core of the application is a Retrieval-Augmented Generation (RAG) pipeline.

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
Generate Embeddings using Gemini
    │
    ▼
Store Vectors in PostgreSQL (pgvector)
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

## 🔍 How RAG Works

### 1. Document Upload

An authenticated user uploads a PDF through the document upload API.

The application:

- Validates the uploaded file.
- Generates a unique stored filename.
- Saves the PDF locally.
- Stores document metadata in PostgreSQL.
- Extracts text using Apache PDFBox.
- Splits the extracted text into chunks.
- Generates embeddings using Google's Gemini Embedding API.
- Stores embeddings and document metadata in PostgreSQL using pgvector.

### 2. User Question

The user sends a question through:

```http
POST /api/chat/ask
```

The question is converted into an embedding using Gemini.

### 3. Semantic Search

The question embedding is passed to PostgreSQL with pgvector.

PostgreSQL performs cosine similarity search to retrieve the most relevant document chunks.

The application currently uses:

```text
Top K = 3
```

Retrieval is restricted to documents belonging to the authenticated user.

### 4. Context Construction

The retrieved chunks are combined into a context that is passed to the LLM.

The prompt instructs the model to answer using the retrieved document context.

If no relevant chunks are found, the application returns an appropriate response indicating that relevant information could not be found in the uploaded documents.

### 5. LLM Generation

The retrieved context and user's question are sent to Groq for answer generation.

The generated answer is saved as an assistant message and returned to the client.

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
| Vector Database | PostgreSQL with pgvector |
| Embeddings | Google Gemini API (`gemini-embedding-001`) |
| Embedding Dimensions | 768 |
| LLM | Groq API |
| PDF Processing | Apache PDFBox |
| API Documentation | SpringDoc OpenAPI / Swagger |
| Containerization | Docker / Docker Compose |
| Cloud Deployment | Render |
| Managed Database | Neon PostgreSQL |
| Testing | JUnit, Spring Boot Test |

## 📁 Project Structure

```text
ai-chatbot-saas/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/riya/aichatbot/
│   │   │       ├── ai/
│   │   │       ├── auth/
│   │   │       ├── chat/
│   │   │       ├── config/
│   │   │       ├── conversation/
│   │   │       ├── document/
│   │   │       ├── exception/
│   │   │       └── user/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       └── application.yml
│   │
│   └── test/
│
├── uploads/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── .gitignore
└── README.md
```

## 🔐 Authentication

Authentication is implemented using Spring Security and JWT.

Passwords are hashed using BCrypt before being stored in PostgreSQL.

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

## 📄 Document APIs

### Base URL

```text
/api/v1/documents
```

### Upload PDF

```http
POST /api/v1/documents/upload
```

Content type:

```text
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
Gemini Embeddings
 ↓
PostgreSQL (pgvector)
```

### List Documents

```http
GET /api/v1/documents
```

Requires authentication.

Returns documents belonging to the authenticated user.

### Delete Document

```http
DELETE /api/v1/documents/{id}
```

Requires authentication.

Deletes the specified document belonging to the authenticated user, including its associated document chunks.

## 💬 Chat APIs

### Base URL

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

The actual response fields depend on the API response model.

## 🗨️ Conversation APIs

### Base URL

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

Conversation ownership is validated before retrieving or modifying conversation resources.

## 🗃️ Database Design

The application uses PostgreSQL for persistent application data and vector storage.

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
└── uploaded_at
```

Documents belong to individual users.

### Document Chunks

```text
document_chunks
├── id
├── document_id
├── user_id
├── chunk_index
├── content
├── embedding
└── created_at
```

The `embedding` column uses the pgvector `VECTOR(768)` data type.

Document chunks are associated with their parent documents and users.

## 🐳 Docker Infrastructure

The project uses Docker Compose for local PostgreSQL infrastructure.

| Service | Image | Port |
|---|---|---|
| PostgreSQL with pgvector | `pgvector/pgvector:pg16` | `5434 → 5432` |

### Start Infrastructure

```bash
docker compose up -d
```

### Check Running Containers

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

For PostgreSQL:

```bash
docker compose logs -f postgres
```

## ⚙️ Configuration

Configure the following environment variables before starting the application.

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key used for JWT signing |
| `GEMINI_API_KEY` | Google Gemini API key |
| `GROQ_API_KEY` | Groq API key |
| `GROQ_BASE_URL` | Groq API base URL |
| `GROQ_MODEL` | Groq model identifier |

### Example Configuration

For local development:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/mydatabase
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

JWT_SECRET=your_generated_jwt_secret

GEMINI_API_KEY=your_gemini_api_key

GROQ_API_KEY=your_groq_api_key
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=openai/gpt-oss-20b
```

Provide your own API keys and a sufficiently long, randomly generated JWT secret.

Never commit `.env` or actual API keys to GitHub.

## 🔑 JWT Configuration

JWT configuration is provided through:

```env
JWT_SECRET=your_generated_jwt_secret
```

The application uses JWT for stateless authentication.

JWT expiration is configured for:

```text
86400000 ms
```

which corresponds to:

```text
24 hours
```

## 🗄️ PostgreSQL Configuration

The application connects to PostgreSQL using:

```text
jdbc:postgresql://localhost:5434/mydatabase
```

Default local development configuration:

```text
Host:     localhost
Port:     5434
Database: mydatabase
User:     postgres
```

The Docker container internally exposes PostgreSQL on port `5432`, while the host uses port `5434`.

For production, the application connects to Neon PostgreSQL through environment-based configuration.

## 🧬 Database Migrations

Flyway is used for version-controlled database schema management.

Migrations are located under:

```text
src/main/resources/db/migration/
```

Flyway configuration:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    out-of-order: false
    validate-on-migrate: true
    baseline-on-migrate: true
    baseline-version: 0
```

On application startup, Flyway validates the migration history and applies pending migrations.

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/riyanikam05/ai-chatbot-saas.git
cd ai-chatbot-saas
```

### 2. Configure Environment Variables

Create a `.env` file in the project root for local configuration.

Example:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/mydatabase
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

JWT_SECRET=your_generated_jwt_secret

GEMINI_API_KEY=your_gemini_api_key

GROQ_API_KEY=your_groq_api_key
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=openai/gpt-oss-20b
```

Use your own credentials. Do not commit this file.

### 3. Start PostgreSQL

```bash
docker compose up -d
```

Verify:

```bash
docker compose ps
```

### 4. Build the Application

**Windows:**

```powershell
.\mvnw.cmd clean package
```

**Linux / macOS:**

```bash
./mvnw clean package
```

### 5. Run the Application

Set the required environment variables in your terminal.

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

Flyway automatically validates and applies pending migrations during application startup.

The application runs locally on:

```text
http://localhost:8081
```

## 📖 Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8081/v3/api-docs
```

Swagger provides interactive documentation for the available REST APIs.

JWT bearer authentication is configured in the OpenAPI configuration.

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
```

All other protected application endpoints require authentication.

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

User A cannot access User B's documents or conversations.

Ownership is validated before accessing protected document and conversation resources.

## ⚠️ Error Handling

The application uses a centralized:

```text
GlobalExceptionHandler
```

API errors are returned using a structured response format.

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

- DocumentNotFoundException
- DocumentProcessingException
- ConversationNotFoundException
- EmailAlreadyExistsException
- EmbeddingGenerationException
- GroqException
- PdfExtractionException

## 🧪 Testing

The project includes tests for key backend components and integrations, including:

- Spring Boot testing
- Spring Security testing
- Authentication APIs
- Gemini embedding generation
- Groq response generation
- PostgreSQL pgvector storage and retrieval
- JUnit

Run tests with:

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux / macOS:**

```bash
./mvnw test
```

Tests requiring external API credentials need the corresponding environment variables configured.

## 🧩 Key Components

### Authentication

- AuthController
- AuthService
- User
- UserRepository
- JwtService
- JwtAuthFilter
- SecurityConfig

### Chat

- ChatController
- ChatService
- ChatRequest
- ChatResponse
- ConversationService
- Message

### Documents

- DocumentController
- DocumentService
- PdfExtractorService
- TextChunkingService
- Document

### Embeddings and Vector Search

- GeminiEmbeddingService
- DocumentChunkRepository
- PostgreSQL with pgvector
- Semantic Similarity Search
- Cosine Distance Search

### LLM

- GroqService
- Groq API

The LLM receives the retrieved document context and user question to generate a context-aware response.

## 📐 RAG Configuration

The application uses the following RAG-related configuration:

| Configuration | Value |
|---|---|
| Top K | 3 |
| Embedding Model | `gemini-embedding-001` |
| Embedding Dimensions | 768 |
| Vector Storage | PostgreSQL with pgvector |
| Similarity Metric | Cosine Distance |
| LLM | Groq |

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
7. Gemini embeddings generated
        │
        ▼
8. Embeddings stored in pgvector
        │
        ▼
9. User asks a question
        │
        ▼
10. Question embedding generated
        │
        ▼
11. PostgreSQL performs semantic search
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

## 📐 Design Decisions

### PostgreSQL

PostgreSQL is used for structured application data such as:

- Users
- Conversations
- Messages
- Documents
- Document metadata
- Document chunks

### pgvector

pgvector is used to store vector embeddings directly in PostgreSQL.

This allows relational application data and vector data to be managed within the same database.

Semantic similarity search is performed using cosine distance.

### Gemini

Google Gemini Embedding API is used to generate vector embeddings for document chunks and user queries.

The application uses the `gemini-embedding-001` model with 768-dimensional embeddings.

### Groq

Groq is used for LLM inference after relevant document context has been retrieved.

### Flyway

Flyway provides version-controlled database migrations instead of relying on automatic schema generation.

### JWT

JWT provides stateless authentication for the REST API and supports user-level resource access control.

## 📦 Docker Volumes

Docker volumes are used to persist local PostgreSQL data.

```text
postgres-data
```

This allows database data to survive container restarts.

## ☁️ Deployment

The backend is deployed on Render, with Neon PostgreSQL used as the managed cloud database.

### Deployment Configuration

- Java 21 runtime
- Spring Boot application
- Render web service
- Neon PostgreSQL
- PostgreSQL with pgvector extension
- Flyway database migrations
- Environment-based configuration
- Render-provided application port

### Production Environment Variables

Configure the following variables in the Render dashboard:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
GEMINI_API_KEY
GROQ_API_KEY
```

Never commit production credentials or database connection strings to GitHub.

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
- Validate uploaded file types and sizes.
- Configure appropriate rate limiting.
- Restrict actuator endpoints in production.
- Avoid exposing sensitive exception details.

## 📊 Project Highlights

This project demonstrates practical experience with:

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- REST APIs
- Spring Data JPA
- PostgreSQL
- pgvector
- Flyway
- Docker
- Docker Compose
- Gemini API
- Embeddings
- Vector Databases
- Semantic Search
- RAG
- LLM Integration
- Groq API
- PDF Processing
- Apache PDFBox
- Exception Handling
- OpenAPI / Swagger
- Testing
- Cloud Deployment

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
Cloud Deployment
```

The result is a backend capable of turning user-uploaded PDF documents into an interactive question-answering system using semantic retrieval and LLM-generated responses.

## 👩‍💻 Author

**Riya Nikam**

GitHub:

https://github.com/riyanikam05

Project:

https://github.com/riyanikam05/ai-chatbot-saas

## ⭐ Summary

AI Chatbot SaaS is a Java/Spring Boot backend that implements an end-to-end Retrieval-Augmented Generation workflow.

```text
PDF
 ↓
Text Extraction
 ↓
Chunking
 ↓
Gemini Embeddings
 ↓
PostgreSQL with pgvector
 ↓
Semantic Retrieval
 ↓
Groq LLM
 ↓
Context-Aware Answer
```

It combines backend engineering, authentication, relational databases, document processing, vector search, embeddings, LLM integration, RAG, Dockerized infrastructure, and cloud deployment into a single application.