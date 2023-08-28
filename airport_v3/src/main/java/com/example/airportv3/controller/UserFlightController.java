package com.example.airportv3.controller;

import com.example.airportv3.dto.UserFlightResponseDto;
import com.example.airportv3.entity.enums.UserFlightState;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.StateException;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.service.FlightCrewReadyService;
import com.example.airportv3.service.FlightService;
import com.example.airportv3.service.FlightUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user_flight")
@RequiredArgsConstructor
public class UserFlightController {
    private final FlightCrewReadyService flightCrewReadyService;
    private final FlightUserService flightUserService;

    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients_instruct")
    private UserFlightResponseDto instructClient(@RequestParam Long registerId) throws UserNotFoundException, StateException, FlightNotFoundException {
        UserFlightResponseDto userFlightResponseDto = this.flightUserService.instructClients(registerId);

        if(
                this.flightUserService.checkIfAllPassengersOfFlightHaveStatus(
                        userFlightResponseDto.getFlightId(),
                        UserFlightState.CLIENT_BRIEFED
                )
        ) {
            this.flightCrewReadyService.allClientsHavePassedTheInstructions(userFlightResponseDto.getFlightId());
        }
        return userFlightResponseDto;
    }
    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients_check")
    public UserFlightResponseDto checkClient(
            @RequestParam Long registerId
    ) throws UserNotFoundException, StateException, FlightNotFoundException {
        UserFlightResponseDto userFlightResponseDto = this.flightUserService.checkClient(registerId);
        if(
                this.flightUserService.checkIfAllPassengersOfFlightHaveStatus(
                        userFlightResponseDto.getFlightId(),
                        UserFlightState.CLIENT_CHECKED
                )
        ) {
            this.flightCrewReadyService.checkedReadyClientsBeforeFlight(userFlightResponseDto.getFlightId());
        }
        return userFlightResponseDto;
    }
    @PreAuthorize(value = "hasRole('STEWARD')")
    @PutMapping(value = "/clients_distribute_food")
    public UserFlightResponseDto clientsFoodDistribute(
            @RequestParam Long registerId
    ) throws FlightNotFoundException, StateException, UserNotFoundException {
        UserFlightResponseDto userFlightResponseDto = this.flightUserService.destinationsFoodDistribution(registerId);
        if(
                this.flightUserService.checkIfAllPassengersOfFlightHaveStatus(
                        userFlightResponseDto.getFlightId(),
                        UserFlightState.CLIENT_FOOD_DISTRIBUTED
                )
        ) {
            this.flightCrewReadyService.foodDistribution(userFlightResponseDto.getFlightId());
        }
        return userFlightResponseDto;
    }


    @PreAuthorize(value = "hasAnyRole('STEWARD', 'CHIEF_STEWARD', 'PILOT')")
    @PutMapping(value = "/members_ready")
    public UserFlightResponseDto confirmReadyToFlight(
    ) throws FlightNotFoundException, StateException, UserNotFoundException {
        UserFlightResponseDto userFlightResponseDto = this.flightUserService.confirmFlightReadiness();
        if( this.flightUserService.checkIfAllCrewMembersReadyForFlight(userFlightResponseDto.getFlightId())){
            this.flightCrewReadyService.messageThatAllCrewMembersAreReadyToFly(userFlightResponseDto.getFlightId());
        }
        return userFlightResponseDto;
    }

    @GetMapping(value = "/all_employees")
    public List<UserFlightResponseDto> getAllEmployeesRegistrations(
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) UserFlightState state
    ) throws UserNotFoundException {
        return this.flightUserService.getAllEmployeesRegistrations(flightId,state);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER')")
    @GetMapping(value = "/all_clients")
    public List<UserFlightResponseDto> getAllClientRegistrations(
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) UserFlightState state
    ) throws UserNotFoundException {
        return this.flightUserService.getAllClientRegistrations(flightId,state);
    }

    @PreAuthorize(value = "hasRole('CLIENT')")
    @GetMapping(value = "/client_current_current_flight")
    public UserFlightResponseDto currentFlight() throws UserNotFoundException {
        return flightUserService.currentFlight();
    }
}
