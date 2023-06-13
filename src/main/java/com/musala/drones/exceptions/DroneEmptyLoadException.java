package com.musala.drones.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DroneEmptyLoadException extends ResponseStatusException {
    public DroneEmptyLoadException() {
        super(HttpStatus.NOT_ACCEPTABLE, "Load is empty");
    }
}
