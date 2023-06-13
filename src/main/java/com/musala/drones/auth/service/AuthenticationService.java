package com.musala.drones.auth.service;

import com.musala.drones.auth.AuthenticationRequest;
import com.musala.drones.auth.AuthenticationResponse;
import com.musala.drones.auth.RegisterRequest;
import com.musala.drones.domain.AppUser;
import com.musala.drones.exceptions.UserAlreadyExistsException;
import com.musala.drones.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository uppUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse signUp(RegisterRequest request) {

        if (uppUserRepository.findByEmail(request.getEmail()).isPresent())
            throw new UserAlreadyExistsException(request.getEmail());

        var user = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(AppUser.Role.USER)
                .build();

        uppUserRepository.save(user);
        var jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()));
        var user = uppUserRepository.findByEmail(request.getEmail()).orElseThrow();

        var jwtToken = jwtService.generateJwtToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
