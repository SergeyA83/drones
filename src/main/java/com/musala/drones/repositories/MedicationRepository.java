package com.musala.drones.repositories;

import com.musala.drones.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicationRepository extends JpaRepository<Medication, String> {
    Optional<Medication> findByCode(String code);
}
