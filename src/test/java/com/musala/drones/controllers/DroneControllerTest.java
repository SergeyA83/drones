package com.musala.drones.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musala.drones.dto.DroneDto;
import com.musala.drones.dto.MedicationDto;
import com.musala.drones.services.DroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DroneService droneService;

    private ObjectMapper objectMapper;

    private final String rootEndpoint = "/drones/";

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void registerDrone() throws Exception {
        // given
        DroneDto droneDto = new DroneDto("serialNumber", "Lightweight", 1.0, 40);
        String requestBody = objectMapper.writeValueAsString(droneDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(droneService).register(eq(droneDto));
    }

    @Test
    void registerDrone_illegal_model() throws Exception {
        // given
        DroneDto droneDto = new DroneDto("serialNumber", "incorrectModel", 1.0, 40);
        String requestBody = objectMapper.writeValueAsString(droneDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());

        verify(droneService, never()).register(any(DroneDto.class));
    }

    @Test
    void registerDrone_illegal_weightLimit() throws Exception {
        // given
        DroneDto droneDto = new DroneDto("serialNumber", "Lightweight", 501.0, 40);
        String requestBody = objectMapper.writeValueAsString(droneDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());

        verify(droneService, never()).register(any(DroneDto.class));
    }

    @Test
    void registerDrone_illegal_batteryCapacity() throws Exception {
        // given
        DroneDto droneDto = new DroneDto("serialNumber", "Lightweight", 500.0, 140);
        String requestBody = objectMapper.writeValueAsString(droneDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());

        verify(droneService, never()).register(any(DroneDto.class));
    }

    @Test
    void registerDrone_illegal_serialNumber() throws Exception {
        // given
        var tooLongSerialNumber = IntStream.range(0, 501)
                .mapToObj(i -> "a")
                .collect(Collectors.joining());
        DroneDto droneDto = new DroneDto(tooLongSerialNumber, "Lightweight", 500.0, 100);
        String requestBody = objectMapper.writeValueAsString(droneDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError());

        verify(droneService, never()).register(any(DroneDto.class));
    }

    @Test
    void checkBattery() throws Exception {
        // given
        Integer batteryCapacity = 50;
        String serialNumber = "serialNumber";
        when(droneService.checkBattery(eq(serialNumber))).thenReturn(batteryCapacity);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(rootEndpoint + "check_battery/{serialNumber}", serialNumber))
                .andExpect(status().isOk());

        verify(droneService).checkBattery(eq(serialNumber));
    }

    @Test
    void getIdles() throws Exception {
        // given
        List<DroneDto> idleDrones = new ArrayList<>();
        idleDrones.add(new DroneDto("serialNumber", "model", 1.0, 40));
        when(droneService.getIdles()).thenReturn(idleDrones);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(rootEndpoint + "get_idles"))
                .andExpect(status().isOk());

        verify(droneService).getIdles();
    }

    @Test
    void load() throws Exception {
        // given
        String serialNumber = "serialNumber";
        List<String> medicationsCodeList = new ArrayList<>();
        medicationsCodeList.add("Code");
        String requestBody = objectMapper.writeValueAsString(medicationsCodeList);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "load/{serialNumber}", serialNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(droneService).load(eq(serialNumber), eq(medicationsCodeList));
    }

    @Test
    void unload() throws Exception {
        // given
        String serialNumber = "serialNumber";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(rootEndpoint + "unload/{serialNumber}", serialNumber))
                .andExpect(status().isOk());

        verify(droneService).unload(eq(serialNumber));
    }

    @Test
    void checkLoads() throws Exception {
        // given
        String serialNumber = "serialNumber";
        List<MedicationDto> loads = new ArrayList<>();
        loads.add(new MedicationDto("Code", "Medication", 0.5, new byte[]{}));
        when(droneService.getItems(eq(serialNumber))).thenReturn(loads);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(rootEndpoint + "get_loads/{serialNumber}", serialNumber))
                .andExpect(status().isOk());

        verify(droneService).getItems(eq(serialNumber));
    }
}