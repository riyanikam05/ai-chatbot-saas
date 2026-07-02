# AI Chatbot

A full-stack AI chatbot application I built using Spring Boot and React.
It uses Ollama to power the conversations with local AI models.

## What it does
- Users can register and log in
- Start multiple chat conversations
- Chat with an AI assistant (powered by Ollama with Llama 3.2)
- Conversation history is saved
- Rate limiting (20 messages per minute per user)

## Tech Stack
**Backend:** Java 21, Spring Boot 3.2.5, PostgreSQL, Redis, JWT Auth
**Frontend:** React (TypeScript), CSS
**AI:** Ollama (local AI models - Llama 3.2)
**Infrastructure:** Docker, Docker Compose

## How to run locally

### Prerequisites
- Java 21
- Docker + Docker Compose

### Steps
1. Clone the repo
2. Create a `.env` file:
   ```
   JWT_SECRET=any_long_random_string_here
   OLLAMA_MODEL=llama3.2
   ```
   (You can copy `.env.example` and modify it)
3. Run: `docker compose up`
4. Pull the Ollama model inside the container:
   ```bash
   docker exec -it ai-chatbot-saas-ollama-1 ollama pull llama3.2
   ```
5. Open http://localhost:3000

### Run backend separately (for development)
1. Start docker compose for the DB and Ollama:
   `docker compose up postgres redis ollama`
2. Pull the model in the Ollama container:
   `docker exec -it ai-chatbot-saas-ollama-1 ollama pull llama3.2`
3. Set env vars and run:
   `mvn spring-boot:run`
4. API docs at: http://localhost:8080/swagger-ui.html

### Run frontend separately (for development)
1. Start backend on port 8080
2. In frontend folder:
   ```bash
   npm install
   npm start
   ```
3. Open http://localhost:3000

## API Endpoints
POST   /api/auth/register
POST   /api/auth/login
GET    /api/auth/me
POST   /api/conversations
GET    /api/conversations
DELETE /api/conversations/{id}
GET    /api/conversations/{id}/messages
POST   /api/conversations/{id}/messages

## What I learned building this
- How JWT authentication works end-to-end
- Integrating local AI models from a Java backend
- Managing conversation context for an LLM
- Redis for lightweight rate limiting
- Connecting a React frontend to a Spring Boot API

## Known limitations / future work
- [ ] Streaming responses (currently waits for full response)
- [ ] Google OAuth login (backend configured, frontend not wired up yet)
- [ ] Image upload support
- [ ] Mobile app version
- [ ] More AI model options in Ollama
