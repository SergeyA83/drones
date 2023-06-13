package com.musala.drones.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UserAlreadyExistsException extends ResponseStatusException {
    private final String email;

    public UserAlreadyExistsException(String email) {
        super(HttpStatus.NOT_FOUND, String.format("User with email %s already registered", email));
        this.email = email;
    }
}
