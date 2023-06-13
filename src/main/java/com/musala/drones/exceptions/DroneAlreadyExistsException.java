package com.musala.drones.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DroneAlreadyExistsException extends ResponseStatusException {
    public DroneAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "Drone already exists");
    }
}
