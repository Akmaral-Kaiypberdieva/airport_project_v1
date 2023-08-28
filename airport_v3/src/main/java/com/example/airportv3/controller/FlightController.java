package com.example.airportv3.controller;

import com.example.airportv3.dto.FlightResponseDto;
import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.StateException;
import com.example.airportv3.service.FlightCrewReadyService;
import com.example.airportv3.service.FlightService;
import com.example.airportv3.service.FlightUserService;
import com.example.airportv3.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("flight")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;
    private final FlightUserService flightUserService;
    private final FlightCrewReadyService flightCrewReadyService;
    private final SeatService seatService;

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/initiate_start_flight")
    public ResponseEntt initiateFlightStart(
            @RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.initiateFlightStart(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_STEWARD')")
    @PutMapping(value = "/assign_distribution_food")
    public ResponseEntt destinationsFoodDistribution(
            @RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.destinationsFoodDistribution(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_DISPATCHER')")
    @PutMapping(value = "/confirm_departure_flight")
    public ResponseEntt confirmDepartureFlight(Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.confirmDepartureFlight(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/departure_initiate")
    public ResponseEntt initiateDeparture(Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.initiateDeparture(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_STEWARD')")
    @PutMapping(value = "/client_readiness")
    public ResponseEntt clientReadyForFlight(
            @RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightCrewReadyService.confirmClientsReadiness(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @PutMapping(value = "/confirm_refueling_for_plane")
    public ResponseEntt confirmationForRefuelingTheAircraft(
            @RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.confirmationForRefuelingTheAircraft(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/preparing_for_flight")
    public ResponseEntt startPreparingForTheFlightDeparture(
            @RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.startPreparingForTheFlightDeparture(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_DISPATCHER')")
    @PutMapping(value = "/confirmation_registration_for_flight")
    public ResponseEntt confirmationOfCheckinsForFlight(@RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightCrewReadyService.confirmationOfCheckinsForFlight(flightId);
    }

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/request_for_landing_aircraft")
    public ResponseEntt requestToLandTheAircraft(@RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.requestToLandTheAircraft(flightId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/assign_landing")
    public ResponseEntt assignLanding(@RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.assignLanding(flightId);
    }

    @PreAuthorize(value = "hasRole('MAIN_DISPATCHER')")
    @PutMapping(value = "/confirm-landing")
    public ResponseEntt confirmationOfTheLandingOfAircraft(@RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.confirmationOfTheLandingOfAircraft(flightId);
    }

    @PreAuthorize(value = "hasRole('PILOT')")
    @PutMapping(value = "/flight_end")
    public ResponseEntt endFlight(@RequestParam Long flightId) throws FlightNotFoundException, StateException {
        return flightService.endFlight(flightId);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'MAIN_DISPATCHER', 'DISPATCHER', 'PILOT')")
    @GetMapping(value = "/all")
    public List<FlightResponseDto> allFlights(@RequestParam FlightState flightState) throws FlightNotFoundException {
        return this.flightService.allFlights(flightState);
    }

    @PreAuthorize(value = "hasRole('MAIN_STEWARD')")
    @PutMapping(value = "/assign_briefing")
    public ResponseEntt assignBriefing(Long flightId) throws FlightNotFoundException, StateException {
        return this.flightService.assignBriefing(flightId);
    }
}
