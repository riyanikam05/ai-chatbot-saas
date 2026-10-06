package com.riya.aichatbot.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ApiError> handleEmailAlreadyExistsException(
                        EmailAlreadyExistsException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.CONFLICT,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .collect(Collectors.toMap(
                                                fieldError -> fieldError.getField(),
                                                fieldError -> fieldError.getDefaultMessage(),
                                                (existing, replacement) -> existing));

                return ResponseEntity.badRequest().body(
                                Map.of(
                                                "error", "Validation failed",
                                                "details", errors,
                                                "timestamp", LocalDateTime.now()));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
                        ConstraintViolationException ex) {

                Map<String, String> errors = ex.getConstraintViolations()
                                .stream()
                                .collect(Collectors.toMap(
                                                violation -> violation.getPropertyPath().toString(),
                                                ConstraintViolation::getMessage,
                                                (existing, replacement) -> existing));

                return ResponseEntity.badRequest().body(
                                Map.of(
                                                "error", "Validation failed",
                                                "details", errors,
                                                "timestamp", LocalDateTime.now()));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiError> handleBadCredentialsException(
                        BadCredentialsException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid email or password",
                                request);
        }

        @ExceptionHandler(DocumentProcessingException.class)
        public ResponseEntity<ApiError> handleDocumentException(
                        DocumentProcessingException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(PdfExtractionException.class)
        public ResponseEntity<ApiError> handlePdfException(
                        PdfExtractionException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(EmbeddingGenerationException.class)
        public ResponseEntity<ApiError> handleEmbeddingException(
                        EmbeddingGenerationException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_GATEWAY,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(ChromaException.class)
        public ResponseEntity<ApiError> handleChromaException(
                        ChromaException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_GATEWAY,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(GroqException.class)
        public ResponseEntity<ApiError> handleGroqException(
                        GroqException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_GATEWAY,
                                ex.getMessage(),
                                request);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiError> handleAccessDeniedException(
                        AccessDeniedException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.FORBIDDEN,
                                "Access denied",
                                request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred.",
                                request);
        }

        private ResponseEntity<ApiError> buildError(
                        HttpStatus status,
                        String message,
                        HttpServletRequest request) {

                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

}