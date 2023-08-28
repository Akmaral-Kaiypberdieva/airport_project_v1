package com.example.airportv3.controller;

import com.example.airportv3.dto.AircraftRegisteredDto;
import com.example.airportv3.dto.AircraftResponseDto;
import com.example.airportv3.exception.AircraftIllegalArgumentException;
import com.example.airportv3.exception.PartNotFoundException;
import com.example.airportv3.service.AircraftRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("plane_register")
public class AircraftRegisterController {
    private final AircraftRegisterService aircraftRegisterService;

    @Autowired
    public AircraftRegisterController(AircraftRegisterService aircraftRegisterService) {
        this.aircraftRegisterService = aircraftRegisterService;
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public AircraftResponseDto registerAircraft(
            @RequestBody AircraftRegisteredDto aircraftRegisteredDto
    ) throws AircraftIllegalArgumentException, PartNotFoundException {
        return this.aircraftRegisterService.registerAircraft(aircraftRegisteredDto);
    }
}
