package com.riya.aichatbot.auth.controller;

import com.riya.aichatbot.auth.dto.AuthResponse;
import com.riya.aichatbot.auth.dto.LoginRequest;
import com.riya.aichatbot.auth.dto.RegisterRequest;
import com.riya.aichatbot.auth.dto.RegisterResponse;
import com.riya.aichatbot.auth.entity.User;
import com.riya.aichatbot.auth.repository.UserRepository;
import com.riya.aichatbot.auth.service.JwtService;
import com.riya.aichatbot.exception.EmailAlreadyExistsException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.riya.aichatbot.exception.EmailAlreadyExistsException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration, login and profile APIs")
public class AuthController {

        private final UserRepository userRepository;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;

        public AuthController(
                        UserRepository userRepository,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        PasswordEncoder passwordEncoder) {

                this.userRepository = userRepository;
                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.passwordEncoder = passwordEncoder;
        }

        @Operation(summary = "Register a new user", description = "Creates a new account with name, email and password.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                        @ApiResponse(responseCode = "409", description = "Email already exists", content = @Content)
        })
        @PostMapping("/register")
        public ResponseEntity<RegisterResponse> register(
                        @Valid @RequestBody RegisterRequest request) {

                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new EmailAlreadyExistsException("Email is already registered.");
                }

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .build();

                userRepository.save(user);

                return ResponseEntity.ok(
                                RegisterResponse.builder()
                                                .message("Email registered successfully")
                                                .build());
        }

        @Operation(summary = "Login", description = "Authenticates the user and returns a JWT token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
        })
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @Valid @RequestBody LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String token = jwtService.generateToken(userDetails);

                return ResponseEntity.ok(
                                AuthResponse.builder()
                                                .token(token)
                                                .userId(user.getId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .build());
        }

        @Operation(summary = "Get current user", description = "Returns the authenticated user's profile.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User profile returned successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        })
        @GetMapping("/me")
        public ResponseEntity<AuthResponse> getCurrentUser(
                        Authentication authentication) {

                User user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String token = jwtService.generateToken(user);

                return ResponseEntity.ok(
                                AuthResponse.builder()
                                                .token(token)
                                                .userId(user.getId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .build());
        }
}