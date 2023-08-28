package com.example.airportv3.controller;

import com.example.airportv3.dto.FlightRequestDto;
import com.example.airportv3.dto.FlightResponseDto;
import com.example.airportv3.exception.AircraftException;
import com.example.airportv3.exception.AircraftNotFoundException;
import com.example.airportv3.service.FlightRegisterService;
import com.example.airportv3.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flight_register")
@RequiredArgsConstructor
public class FlightRegisterController {
    private final FlightRegisterService flightRegisterService;
    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public FlightResponseDto registerFlight(
            @RequestBody FlightRequestDto flightRequestDto) throws AircraftNotFoundException, AircraftException {
        return flightRegisterService.registerFlight(flightRequestDto);
    }
}
