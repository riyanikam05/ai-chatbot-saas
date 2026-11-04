package com.riya.aichatbot.exception;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(String message) {
        super(message);
    }
}