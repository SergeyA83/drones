package com.musala.drones.controllers;

import com.musala.drones.dto.DroneDto;
import com.musala.drones.dto.MedicationDto;
import com.musala.drones.dto.ResponseDto;
import com.musala.drones.services.DroneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Drones", description = "Drones manipulating controller")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/drones")
@AllArgsConstructor
public class DroneController {
    private final DroneService droneService;

    @PostMapping("/register")
    @Operation(summary = "Registering a new drone",
            description = "Registering a new drone if drone with such serial number doesn't exists")
    public ResponseEntity<ResponseDto> registerDrone(@Valid @RequestBody DroneDto droneDto) {
        droneService.register(droneDto);
        return new ResponseEntity<>(new ResponseDto("success", "Drone registered"), HttpStatus.CREATED);
    }

    @GetMapping("/check_battery/{serialNumber}")
    @Operation(summary = "Check drone battery level for a given drone")
    public ResponseEntity<Integer> checkBattery(@PathVariable String serialNumber) {
        var batteryCapacity = droneService.checkBattery(serialNumber);
        return ResponseEntity.ok(batteryCapacity);
    }

    @GetMapping("/get_idles")
    @Operation(summary = "Checking available drones for loading",
            description = "Retrieving all drones list witch status is IDLE")
    public ResponseEntity<List<DroneDto>> getIdles() {
        var idleDrones = droneService.getIdles();
        return ResponseEntity.ok(idleDrones);
    }

    @CacheEvict(value = "loads", key="#serialNumber")
    @PostMapping("/load/{serialNumber}")
    @Operation(summary = "Loading a drone with medication items",
               description = """
                       Loading a drone with existing medication items. Possible only in IDLE status.\s
                       Existing medications codes:
                       VIAG001;CIAL002;TYLE003;ASPI004;IBUP005;ACET006;ADVI007;ALEV008;AMBI009;ATIV010;NAPE011;PLAV012;LISI013;WELL014;ZEST015;LIPIT016;LORA017;PROZ018;SERT019;ZOLO020""")
    public ResponseEntity<ResponseDto> load(@PathVariable String serialNumber,
                                            @RequestBody @Valid List<String> medicationsCodeList) {
        droneService.load(serialNumber, medicationsCodeList);
        return ResponseEntity.ok(new ResponseDto("success", "Drone loaded"));
    }

    @CacheEvict(value = "loads", key="{#serialNumber}")
    @PostMapping("/unload/{serialNumber}")
    @Operation(summary = "Unloading a drone with medication items",
            description = "Unloading a drone with medication items, possible only in IDLE and LOADED status")
    public ResponseEntity<ResponseDto> unload(@PathVariable String serialNumber) {
        droneService.unload(serialNumber);
        return ResponseEntity.ok(new ResponseDto("success", "Drone unloaded"));
    }

    @Cacheable(value = "loads", key="{#serialNumber}")
    @GetMapping("/get_loads/{serialNumber}")
    @Operation(summary = "Checking loaded medication items for a given drone",
            description = "Extracting list of loaded medication items for a given drone by it's serial number")
    public ResponseEntity<List<MedicationDto>> checkLoads(@PathVariable String serialNumber) {
        var loads = droneService.getItems(serialNumber);
        return ResponseEntity.ok(loads);
    }

    @CacheEvict(value = "loads", key="#serialNumber")
    @PostMapping("/deliver/{serialNumber}")
    @Operation(summary = "Deliver the drone",
            description = "Deliver the drone with certain serial number. Possible only in LOADED state.")
    public ResponseEntity<ResponseDto> deliver(@PathVariable String serialNumber) {
        droneService.deliver(serialNumber);
        return ResponseEntity.ok(new ResponseDto("success", "Drone delivered"));
    }

    @PostMapping("/return/{serialNumber}")
    @Operation(summary = "Return the drone",
            description = "Return the drone with certain serial number. Possible only in DELIVERED state.")
    public ResponseEntity<ResponseDto> returnDrone(@PathVariable String serialNumber) {
        droneService.returnDrone(serialNumber);
        return ResponseEntity.ok(new ResponseDto("success", "Drone returned"));
    }
}
