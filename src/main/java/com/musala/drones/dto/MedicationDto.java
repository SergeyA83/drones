package com.musala.drones.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {
    @NotNull
    @Pattern(regexp = "[A-Z_0-9]+", message = "Only uppercase letters, underscore and number")
    private String code;
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "Only letters, numbers, ‘-‘, ‘_’ characters are allowed")
    private String name;
    @NotNull
    private double weight;
    private byte[] image;
}
