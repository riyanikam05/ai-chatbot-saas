package com.riya.aichatbot.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        private static final String BEARER_AUTH_SCHEME = "bearerAuth";

        @Bean
        public OpenAPI chatbotOpenAPI() {

                return new OpenAPI()

                                .info(new Info()

                                                .title("AI Chatbot SaaS API")

                                                .version("1.0")

                                                .description("""
                                                                AI-powered RAG chatbot built with Spring Boot.

                                                                Features:
                                                                • JWT Authentication
                                                                • PDF Upload
                                                                • PDF Text Extraction
                                                                • Ollama Embeddings
                                                                • ChromaDB Vector Search
                                                                • Groq LLM Integration
                                                                """)

                                                .contact(new Contact()

                                                                .name("Riya Nikam")

                                                                .email("your-email@example.com"))

                                                .license(new License()

                                                                .name("MIT")))

                                .externalDocs(new ExternalDocumentation()

                                                .description("GitHub Repository")

                                                .url("https://github.com/riyanikam05/ai-chatbot-saas"))

                                // Registers what "bearerAuth" actually means, and enables the
                                // "Authorize" button in Swagger UI so protected endpoints can
                                // be tested directly from the docs page.
                                .components(new Components()
                                                .addSecuritySchemes(BEARER_AUTH_SCHEME,
                                                                new SecurityScheme()
                                                                                .name(BEARER_AUTH_SCHEME)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")))

                                .addSecurityItem(new SecurityRequirement()
                                                .addList(BEARER_AUTH_SCHEME));
        }
}