package com.riya.aichatbot.exception;

public class GroqException extends RuntimeException {

    public GroqException(String message) {
        super(message);
    }

    public GroqException(String message, Throwable cause) {
        super(message, cause);
    }
}