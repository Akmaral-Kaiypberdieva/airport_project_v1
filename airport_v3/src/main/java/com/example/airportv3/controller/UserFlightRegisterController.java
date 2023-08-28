package com.example.airportv3.controller;

import com.example.airportv3.dto.UserFlightRequestDto;
import com.example.airportv3.dto.UserFlightResponseDto;
import com.example.airportv3.exception.*;
import com.example.airportv3.service.UserFlightRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_flight_register")
@RequiredArgsConstructor
public class UserFlightRegisterController {
    private final UserFlightRegisterService userFlightRegisterService;

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/crew_register")
    public UserFlightResponseDto registerClientForFlight(@RequestBody UserFlightRequestDto userFlightRequestDto) throws FlightNotFoundException, FlightException, SeatReservedException, SeatNotFoundException {
        return this.userFlightRegisterService.registerClientForFlight(userFlightRequestDto);
    }

    @PreAuthorize(value = "hasRole('CLIENT')")
    @PutMapping(value = "/cancel_registration")
    public UserFlightResponseDto cancelRegistered(@RequestParam Long registrationId) throws FlightNotFoundException, CancelRegistrationException, SeatReservedException, SeatNotFoundException {
        return this.userFlightRegisterService.cancelRegistered(registrationId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register_members")
    public List<UserFlightResponseDto> registerMembersForFlight
            (@RequestBody List<UserFlightRequestDto> flightRequestDtoList) throws FlightNotFoundException, UserNotFoundException, FlightException {
        return userFlightRegisterService.registerMembersForFlight(flightRequestDtoList);
    }
}
