package com.musala.drones.services;

import com.musala.drones.domain.Drone;
import com.musala.drones.domain.Medication;
import com.musala.drones.dto.DroneDto;
import com.musala.drones.dto.MedicationDto;
import com.musala.drones.exceptions.*;
import com.musala.drones.repositories.DroneRepository;
import com.musala.drones.repositories.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DroneServiceTest {

    @Mock
    private DroneRepository droneRepository;
    @Mock
    private MedicationRepository medicationRepository;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private DroneService droneService;

    @Test
    void register_droneAlreadyExists_exception() {
        //when
        var droneDto = new DroneDto("serialNumber", "model", 200.0, 40);
        when(droneRepository.findBySerialNumber(droneDto.getSerialNumber()))
                .thenReturn(Optional.of(new Drone()));

        //then
        assertThrows(DroneAlreadyExistsException.class, () -> droneService.register(droneDto));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void register_success() {
        //when
        var droneDto = new DroneDto("serialNumber", "model", 200.0, 40);
        var drone = modelMapper.map(droneDto, Drone.class);
        drone.setState(Drone.State.IDLE);
        when(droneRepository.findBySerialNumber(droneDto.getSerialNumber()))
                .thenReturn(Optional.empty());

        when(modelMapper.map(droneDto, Drone.class))
                .thenReturn(drone);

        //then
        assertDoesNotThrow(() -> droneService.register(droneDto));
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    void checkBattery_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.checkBattery(serialNumber));
    }

    @Test
    void checkBattery_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        var batteryCapacityActual = droneService.checkBattery(serialNumber);
        assertEquals(40, batteryCapacityActual);
    }

    @Test
    void getIdles_not_found() {
        //when
        when(droneRepository.findAllByState(Drone.State.IDLE))
                .thenReturn(List.of());
        //then
        var idleDronesActual = droneService.getIdles();
        assertEquals(0, idleDronesActual.size());
    }

    @Test
    void getIdles_success() {
        //when
        var droneDtoList = List.of(new Drone("serialNumber1", "model1", 200.0, 40, Drone.State.IDLE, new ArrayList<>()),
                new Drone("serialNumber2", "model2", 300.0, 50, Drone.State.IDLE, new ArrayList<>()),
                new Drone("serialNumber3", "model3", 400.0, 60, Drone.State.IDLE, new ArrayList<>()));
        when(droneRepository.findAllByState(Drone.State.IDLE))
                .thenReturn(droneDtoList);

        var idleDroneDtoListExpected = droneDtoList.stream().map(d -> modelMapper.map(d, DroneDto.class)).toList();

        //then
        var idleDroneDtoListActual = droneService.getIdles();
        assertEquals(idleDroneDtoListExpected, idleDroneDtoListActual);
    }

    @Test
    void load_emptyLoad_exception() {
        //when
        String serialNumber = "serialNumber";
        //then
        assertThrows(DroneEmptyLoadException.class, () -> droneService.load(serialNumber, List.of()));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.load(serialNumber, List.of("Code")));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_droneIllegalCurrentState_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADED, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        //then
        var exceptionActual = assertThrows(DroneIllegalCurrentStateException.class, () -> droneService.load(serialNumber, List.of("Code")));
        assertEquals(Drone.State.LOADED, exceptionActual.getCurrentState());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_droneBatteryCapacityTooLow_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 20, Drone.State.IDLE, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        var exceptionActual = assertThrows(DroneBatteryCapacityTooLowException.class, () -> droneService.load(serialNumber, List.of("Code")));
        assertEquals(25, exceptionActual.getMinCapacity());
        assertEquals(drone.getBatteryCapacity(), exceptionActual.getCurrentCapacity());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_droneLoadLimitException_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());

        var medicationsCodeList = List.of(
                "code1",
                "code2"
        );

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        when(medicationRepository.findByCode("code1"))
                .thenReturn(Optional.of(new Medication("code1", "name1", 100.0, null)));

        when(medicationRepository.findByCode("code2"))
                .thenReturn(Optional.of(new Medication("code2", "name2", 300.0, null)));
        //then
        var exceptionActual = assertThrows(DroneLoadLimitException.class, () -> droneService.load(serialNumber, medicationsCodeList));
        assertEquals(drone.getWeightLimit(), exceptionActual.getLoadLimit());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_medicationNotFoundException_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());

        var medicationExistedCode = "code1";
        var medicationNonExistedCode = "code2";

        var medicationsCodeList = List.of(
                medicationExistedCode,
                medicationNonExistedCode
        );

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        when(medicationRepository.findByCode(eq(medicationExistedCode)))
                .thenReturn(Optional.of(new Medication()));

        when(medicationRepository.findByCode(eq(medicationNonExistedCode)))
                .thenReturn(Optional.empty());
        //then
        var exceptionActual = assertThrows(MedicationNotFoundException.class, () -> droneService.load(serialNumber, medicationsCodeList));
        assertEquals(medicationNonExistedCode, exceptionActual.getCode());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void load_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());

        var medicationCode1 = "code1";
        var medicationCode2 = "code2";
        var medicationDto1 = new MedicationDto(medicationCode1, "name1", 100.0, new byte[]{});
        var medicationDto2 = new MedicationDto(medicationCode2, "name2", 60.0, new byte[]{});
        var medication1 = modelMapper.map(medicationDto1, Medication.class);
        var medication2 = modelMapper.map(medicationDto2, Medication.class);

        var medicationsCodeList = List.of(
                medicationCode1,
                medicationCode2
        );

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        when(medicationRepository.findByCode(eq(medicationCode1)))
                .thenReturn(Optional.of(medication1));

        when(medicationRepository.findByCode(eq(medicationCode2)))
                .thenReturn(Optional.of(medication2));

        //then
        assertDoesNotThrow(() -> droneService.load(serialNumber, medicationsCodeList));
        verify(droneRepository, times(1)).save(drone);

        var medicationListExpected = List.of(medication1, medication2);

        assertEquals(medicationListExpected, drone.getLoadedItems());
        assertEquals(Drone.State.LOADED, drone.getState());
    }

    @Test
    void unload_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.unload(serialNumber));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void unload_droneIllegalCurrentState_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADING, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        var exceptionActual = assertThrows(DroneIllegalCurrentStateException.class, () -> droneService.unload(serialNumber));
        assertEquals(Drone.State.LOADING, exceptionActual.getCurrentState());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void unload_loaded_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADED, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        assertDoesNotThrow(() -> droneService.unload(serialNumber));
        verify(droneRepository, times(1)).save(drone);
        assertEquals(Drone.State.IDLE, drone.getState());
        assertEquals(0, drone.getLoadedItems().size());
    }

    @Test
    void unload_idle_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        //then
        assertDoesNotThrow(() -> droneService.unload(serialNumber));
        verify(droneRepository, never()).save(any(Drone.class));
        assertEquals(Drone.State.IDLE, drone.getState());
        assertEquals(0, drone.getLoadedItems().size());
    }

    @Test
    void getItems_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.getItems(serialNumber));
    }

    @Test
    void getItems_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.IDLE, new ArrayList<>());
        var medicationCode1 = "code1";
        var medicationCode2 = "code2";
        var medicationDto1 = new MedicationDto(medicationCode1, "name1", 100.0, new byte[]{});
        var medicationDto2 = new MedicationDto(medicationCode2, "name2", 60.0, new byte[]{});
        var medication1 = modelMapper.map(medicationDto1, Medication.class);
        var medication2 = modelMapper.map(medicationDto2, Medication.class);

        var medicationDtoList = List.of(
                medicationDto1,
                medicationDto2
        );

        drone.setLoadedItems(List.of(medication1, medication2));

        //then
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        var medicationListActual = droneService.getItems(serialNumber);
        assertEquals(medicationListActual, medicationDtoList);
    }

    @Test
    void deliver_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.deliver(serialNumber));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void deliver_droneIllegalCurrentState_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADING, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        var exceptionActual = assertThrows(DroneIllegalCurrentStateException.class, () -> droneService.deliver(serialNumber));
        assertEquals(Drone.State.LOADING, exceptionActual.getCurrentState());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void deliver_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADED, new ArrayList<>());
        var medication1 = new Medication("code1", "name1", 100.0, new byte[]{});
        var medication2 = new Medication("code2", "name2", 60.0, new byte[]{});
        drone.setLoadedItems(List.of(medication1, medication2));

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        //then
        assertDoesNotThrow(() -> droneService.deliver(serialNumber));
        verify(droneRepository, times(1)).save(drone);
        assertEquals(Drone.State.DELIVERED, drone.getState());
        assertEquals(0, drone.getLoadedItems().size());
    }

    @Test
    void returnDrone_droneNotFound_exception() {
        //when
        String serialNumber = "serialNumber";
        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.empty());
        //then
        assertThrows(DroneNotFoundException.class, () -> droneService.returnDrone(serialNumber));
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void returnDrone_droneIllegalCurrentState_exception() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.LOADING, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));
        //then
        var exceptionActual = assertThrows(DroneIllegalCurrentStateException.class, () -> droneService.returnDrone(serialNumber));
        assertEquals(Drone.State.LOADING, exceptionActual.getCurrentState());
        verify(droneRepository, never()).save(any(Drone.class));
    }

    @Test
    void returnDrone_success() {
        //when
        String serialNumber = "serialNumber";
        var drone = new Drone(serialNumber, "model", 200.0, 40, Drone.State.DELIVERED, new ArrayList<>());

        when(droneRepository.findBySerialNumber(eq(serialNumber)))
                .thenReturn(Optional.of(drone));

        //then
        assertDoesNotThrow(() -> droneService.returnDrone(serialNumber));
        verify(droneRepository, times(1)).save(drone);
        assertEquals(Drone.State.IDLE, drone.getState());
        assertEquals(0, drone.getLoadedItems().size());
    }
}