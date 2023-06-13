package com.musala.drones.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"loadedItems"})
public class Drone {
    @Id
    @Column(length = 100)
    private String serialNumber;
    @NotNull
    private String model;
    @NotNull
    private double weightLimit;
    private int batteryCapacity;
    @Enumerated(value = EnumType.STRING)
    private State state;

    public enum State {
        IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Medication> loadedItems;
}