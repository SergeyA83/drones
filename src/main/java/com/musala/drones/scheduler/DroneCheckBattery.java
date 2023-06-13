package com.musala.drones.scheduler;

import com.musala.drones.repositories.DroneRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
@EnableAsync
@AllArgsConstructor
public class DroneCheckBattery {

    private final DroneRepository droneRepository;

    @Async
    @Scheduled(fixedRate = 5000)
    public void check() {
        droneRepository.findAll().forEach(d -> log.info(String.format("Drone %s current battery capacity: %d", d.getSerialNumber(), d.getBatteryCapacity())));
    }
}
