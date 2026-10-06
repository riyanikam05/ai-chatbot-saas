# AI Chatbot SaaS

> An AI-powered document question-answering platform built with Spring Boot, RAG, Ollama, ChromaDB, and Groq.

AI Chatbot SaaS is a backend application that allows authenticated users to upload PDF documents and ask questions about their uploaded content.

The application uses a **Retrieval-Augmented Generation (RAG)** pipeline to retrieve relevant document chunks using vector similarity search and generate grounded answers using an LLM.

It also provides JWT-based authentication, conversation management, document management, PostgreSQL persistence, Flyway database migrations, Dockerized infrastructure, and OpenAPI/Swagger documentation.

---

## ✨ Features

- 🔐 JWT-based user authentication
- 👤 User registration and login
- 🔒 Protected REST APIs
- 📄 PDF document upload
- 📖 PDF text extraction using Apache PDFBox
- ✂️ Text chunking for document processing
- 🧠 Embedding generation using Ollama
- 🔎 Semantic vector search using ChromaDB
- 🤖 LLM-based answer generation using Groq
- 📚 Retrieval-Augmented Generation (RAG)
- 💬 Conversation management
- 📝 Persistent chat messages
- 👥 User-specific document retrieval
- 🔐 User ownership checks for conversations and documents
- 🗄️ PostgreSQL database
- 🛠️ Flyway database migrations
- 📑 OpenAPI / Swagger API documentation
- ⚠️ Centralized exception handling
- 🐳 Dockerized PostgreSQL, ChromaDB, Redis and Ollama infrastructure
- 📊 Spring Boot Actuator health and application endpoints

---

## 🏗️ Architecture

The application follows a layered Spring Boot architecture with separate modules for authentication, document processing, chat, AI services, configuration, and exception handling.

```text
                         ┌─────────────────────┐
                         │       Client        │
                         │  Postman / Swagger  │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │    Spring Boot      │
                         │     REST API        │
                         └──────────┬──────────┘
                                    │
                 ┌──────────────────┼──────────────────┐
                 │                  │                  │
                 ▼                  ▼                  ▼
          ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
          │    Auth     │    │  Documents  │    │    Chat     │
          │   Module    │    │   Module    │    │   Module    │
          └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
                 │                  │                  │
                 ▼                  ▼                  ▼
          ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
          │    JWT      │    │ PDFBox      │    │ RAG Pipeline│
          │ Security    │    │  Extraction │    │             │
          └─────────────┘    └──────┬──────┘    └──────┬──────┘
                                    │                  │
                                    ▼                  ▼
                             ┌─────────────┐    ┌─────────────┐
                             │   Chunking  │    │   Ollama    │
                             └──────┬──────┘    │  Embeddings │
                                    │           └──────┬──────┘
                                    │                  │
                                    └────────┬─────────┘
                                             ▼
                                      ┌─────────────┐
                                      │  ChromaDB   │
                                      │Vector Store │
                                      └──────┬──────┘
                                             │
                                             ▼
                                      ┌─────────────┐
                                      │    Groq     │
                                      │     LLM     │
                                      └─────────────┘

                         ┌──────────────────────────┐
                         │       PostgreSQL         │
                         │ Users / Documents /      │
                         │ Conversations / Messages │
                         └──────────────────────────┘