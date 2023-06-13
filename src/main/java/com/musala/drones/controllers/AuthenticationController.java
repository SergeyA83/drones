package com.musala.drones.controllers;

import com.musala.drones.auth.AuthenticationRequest;
import com.musala.drones.auth.AuthenticationResponse;
import com.musala.drones.auth.RegisterRequest;
import com.musala.drones.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Application users registration and log in")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign_up")
    @Operation(summary = "Registering a new user",
               description = "Registering a new application user if user with such email doesn't exists." +
                       "Getting the JWT bearer token as API key")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/sign_in")
    @Operation(summary = "User login to the application",
            description = "Login to the application as registered user by username (email) and password." +
                    "Getting the JWT bearer token as API key")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));

    }
}
