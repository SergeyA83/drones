package com.musala.drones.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class DroneBatteryCapacityTooLowException extends ResponseStatusException {
    private final int minCapacity;
    private final int currentCapacity;

    public DroneBatteryCapacityTooLowException(int minCapacity, int currentCapacity) {
        super(HttpStatus.NOT_ACCEPTABLE, String.format("Operation impossible, battery capacity lower than %s%%, current capacity is %s%%",
                minCapacity, currentCapacity));
        this.minCapacity = minCapacity;
        this.currentCapacity = currentCapacity;
    }
}
