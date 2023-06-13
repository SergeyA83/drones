package com.musala.drones.repositories;

import com.musala.drones.domain.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, String> {
    Optional<Drone> findBySerialNumber(String serialNumber);

    List<Drone> findAllByState(Drone.State state);
}
