package com.musala.drones.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DroneNotFoundException extends ResponseStatusException {
    public DroneNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Drone not found");
    }
}
