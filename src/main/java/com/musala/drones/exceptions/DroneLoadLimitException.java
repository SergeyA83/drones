package com.musala.drones.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class DroneLoadLimitException extends ResponseStatusException {
    private final double loadLimit;

    public DroneLoadLimitException(double loadLimit) {
        super(HttpStatus.NOT_ACCEPTABLE, String.format("Loading canceled, weight limit %s exceeded", loadLimit));
        this.loadLimit = loadLimit;
    }
}
