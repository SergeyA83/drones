package com.musala.drones.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class MedicationNotFoundException extends ResponseStatusException {
    private final String code;

    public MedicationNotFoundException(String code) {
        super(HttpStatus.NOT_FOUND, String.format("Medication with code %s not found", code));
        this.code = code;
    }
}
