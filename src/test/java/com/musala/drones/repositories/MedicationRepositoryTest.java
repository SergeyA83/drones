package com.musala.drones.repositories;

import com.musala.drones.domain.Medication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MedicationRepositoryTest {
    @Autowired
    MedicationRepository medicationRepository;

    @Test
    void findByCode() {
        // given
        var medication = new Medication("code", "name", 0.7, new byte[]{});
        medicationRepository.save(medication);
        // when
        var optionalMedication = medicationRepository.findByCode(medication.getCode());
        // then
        assertThat(optionalMedication).isPresent().contains(medication);
    }
}