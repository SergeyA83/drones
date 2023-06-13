package com.musala.drones.repositories;

import com.musala.drones.domain.Drone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class DroneRepositoryTest {
    @Autowired
    private DroneRepository droneRepository;

    @Test
    public void findBySerialNumber() {
        // given
        var drone = new Drone("serialNumber", "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());
        droneRepository.save(drone);
        // when
        var optionalDrone = droneRepository.findBySerialNumber("serialNumber");
        // then
        assertThat(optionalDrone).isPresent().contains(drone);
    }

    @Test
    public void findAllByState_whenDronesExist_thenReturnListOfDrones() {
        // given
        var idleDrone = new Drone("serialNumber1", "model1", 200.0, 40, Drone.State.IDLE, new ArrayList<>());
        var loadingDrone = new Drone("serialNumber2", "model2", 300.0, 50, Drone.State.LOADING, new ArrayList<>());

        droneRepository.save(idleDrone);
        droneRepository.save(loadingDrone);

        // when
        List<Drone> drones = droneRepository.findAllByState(Drone.State.IDLE);
        var nonIdleExists = drones.stream().anyMatch(d -> d.getState() != Drone.State.IDLE);
        // then
        assertFalse(nonIdleExists);
        assertThat(drones).contains(idleDrone);
    }
}