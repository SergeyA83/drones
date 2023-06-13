package com.musala.drones.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroneDto {
    @NotNull
    @NotBlank
    @Size(max = 100, message = "Serial number length exceeded length 100")
    private String serialNumber;

    @NotNull
    @NotBlank
    @Pattern(regexp = "Lightweight|Middleweight|Cruiserweight|Heavyweight")
    private String model;

    @NotNull
    @Max(value = 500)
    private double weightLimit;

    @NotNull
    @Range(min = 0, max = 100)
    private int batteryCapacity;
}
