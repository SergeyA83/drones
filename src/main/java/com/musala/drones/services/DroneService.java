package com.musala.drones.services;

import com.musala.drones.domain.Drone;
import com.musala.drones.domain.Medication;
import com.musala.drones.dto.DroneDto;
import com.musala.drones.dto.MedicationDto;
import com.musala.drones.exceptions.*;
import com.musala.drones.repositories.DroneRepository;
import com.musala.drones.repositories.MedicationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class DroneService {
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    private final ModelMapper modelMapper;

    private final EntityManager entityManager;

    public void register(DroneDto droneDto) {
        var droneOptional = droneRepository.findBySerialNumber(droneDto.getSerialNumber());
        if (droneOptional.isPresent())
            throw new DroneAlreadyExistsException();

        var drone = modelMapper.map(droneDto, Drone.class);
        drone.setState(Drone.State.IDLE);
        droneRepository.save(drone);
    }

    public Integer checkBattery(String serialNumber) {
        var drone = findBySerialNumber(serialNumber);
        return drone.getBatteryCapacity();
    }

    public List<DroneDto> getIdles() {
        return droneRepository.findAllByState(Drone.State.IDLE)
                .stream().map(d -> modelMapper.map(d, DroneDto.class)).toList();
    }

    public void load(String serialNumber, List<String> medicationsCodeList) {
        //validations
        if (medicationsCodeList.size() == 0)
            throw new DroneEmptyLoadException();

        var drone = findBySerialNumber(serialNumber);

        var droneState = drone.getState();

        if (drone.getState() != Drone.State.IDLE)
            throw new DroneIllegalCurrentStateException(droneState);

        if (drone.getBatteryCapacity() < 25)
            throw new DroneBatteryCapacityTooLowException(25, drone.getBatteryCapacity());

        var medications = medicationsCodeList
                .stream()
                .map(code -> {
                    var medicationOptional = medicationRepository.findByCode(code);
                    return medicationOptional.orElseThrow(() -> new MedicationNotFoundException(code));
                }).toList();

        var totalWeight = medications
                .stream()
                .mapToDouble(Medication::getWeight).sum();

        if (totalWeight > drone.getWeightLimit())
            throw new DroneLoadLimitException(drone.getWeightLimit());

        //saving
        drone.setLoadedItems(new ArrayList<>(medications));
        drone.setState(Drone.State.LOADING);
        drone.setState(Drone.State.LOADED);
        droneRepository.save(drone);
    }

    public void unload(String serialNumber) {
        var drone = findBySerialNumber(serialNumber);
        var droneState = drone.getState();

        if (drone.getState() != Drone.State.LOADED && drone.getState() != Drone.State.IDLE)
            throw new DroneIllegalCurrentStateException(droneState);

        if (drone.getState() == Drone.State.LOADED) {
            drone.setLoadedItems(new ArrayList<>());
            drone.setState(Drone.State.IDLE);
            droneRepository.save(drone);
        }
    }

    public List<MedicationDto> getItems(String serialNumber) {
        var drone = findBySerialNumber(serialNumber);

        return drone.getLoadedItems()
                .stream()
                .map(m -> modelMapper.map(m, MedicationDto.class))
                .toList();
    }


    public void deliver(String serialNumber) {
        var drone = findBySerialNumber(serialNumber);
        var droneState = drone.getState();

        if (drone.getState() != Drone.State.LOADED)
            throw new DroneIllegalCurrentStateException(droneState);

        drone.setState(Drone.State.DELIVERING);
        drone.setState(Drone.State.DELIVERED);
        drone.setLoadedItems(new ArrayList<>());
        droneRepository.save(drone);
    }

    public void returnDrone(String serialNumber) {
        var drone = findBySerialNumber(serialNumber);
        var droneState = drone.getState();

        if (drone.getState() != Drone.State.DELIVERED)
            throw new DroneIllegalCurrentStateException(droneState);

        drone.setState(Drone.State.RETURNING);
        drone.setState(Drone.State.IDLE);
        droneRepository.save(drone);
    }

    private Drone findBySerialNumber(String serialNumber) {
        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(serialNumber);
        if (optionalDrone.isEmpty()) {
            throw new DroneNotFoundException();
        }
        return optionalDrone.get();
    }
}
