package com.musala.drones.exceptions;

import com.musala.drones.domain.Drone;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class DroneIllegalCurrentStateException extends ResponseStatusException {
    private final Drone.State currentState;

    public DroneIllegalCurrentStateException(Drone.State currentState) {
        super(HttpStatus.NOT_ACCEPTABLE, String.format("Operation with drone is not allowed in %s state", currentState));
        this.currentState = currentState;
    }
}
