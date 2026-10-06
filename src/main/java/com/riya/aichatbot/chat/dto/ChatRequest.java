package com.riya.aichatbot.chat.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

        @NotBlank(message = "Question cannot be empty.")
        private String question;

        // Optional. If null, a new Conversation is created for this question.
        // If provided, the question/answer are appended to that existing
        // conversation (and must belong to the authenticated user).
        private Long conversationId;

        public ChatRequest() {
        }

        public ChatRequest(String question) {
                this.question = question;
        }

        public ChatRequest(String question, Long conversationId) {
                this.question = question;
                this.conversationId = conversationId;
        }

        public String getQuestion() {
                return question;
        }

        public void setQuestion(String question) {
                this.question = question;
        }

        public Long getConversationId() {
                return conversationId;
        }

        public void setConversationId(Long conversationId) {
                this.conversationId = conversationId;
        }
}