package com.musala.drones.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UserNotFoundException extends ResponseStatusException {
    private final String email;

    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, String.format("User with email %s not found", email));
        this.email = email;
    }
}
