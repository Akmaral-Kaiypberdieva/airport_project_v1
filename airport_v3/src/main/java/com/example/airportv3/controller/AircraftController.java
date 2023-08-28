package com.example.airportv3.controller;

import com.example.airportv3.dto.AircraftResponseDto;
import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.exception.*;
import com.example.airportv3.service.AircraftInspectionService;
import com.example.airportv3.service.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/aircraft")
@RequiredArgsConstructor
public class AircraftController {
    private final AircraftInspectionService aircraftInspectionService;
    private final AircraftService aircraftService;

    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @PutMapping(value = "/assign_engineer")
    public String assignTheRefuelingOfTheAircraft(
            @RequestParam Long aircraftId,
            @RequestParam Long engineerId) throws AircraftNotFoundException, RefuelingOfAircraftNoScheduledException, UserNotFoundException, EngineerException{
        return this.aircraftService.assignTheRefuelingOfTheAircraft(aircraftId,engineerId);
    }
    @PreAuthorize(value = "hasRole('ENGINEER')")
    @PutMapping(value = "/{aircraftId}/aircraft_refuel")
    public String refuelingTheAircraft(
           @PathVariable Long aircraftId
    ) throws AircraftNotFoundException, RefuelingOfAircraftNoScheduledException, EngineerException{
        return this.aircraftService.refuelingTheAircraft(aircraftId);
    }


    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @PutMapping(value = "/assign_repairs")
    private ResponseEntt aircraftRepairs(
            @RequestParam Long aircraftId,
            @RequestParam Long engineersId) throws AircraftNotFoundException, EngineerException, InspectionNotFoundException, StateException, UserNotFoundException{
        return this.aircraftInspectionService.assignAircraftRepairs(aircraftId,engineersId);
    }

    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @PutMapping(value = "/serviceability")
    public ResponseEntt confirmTheServiceabilityOfTheAircraft(
            @RequestParam Long aircraftId) throws AircraftNotFoundException, PartInspectionNotFoundException, StateException {
        return this.aircraftService.confirmTheServiceabilityOfTheAircraft(aircraftId);
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PutMapping(value = "/{aircraftId}/send_to_confirmation")
    public ResponseEntt sendTheAircraftToConfirmRegistration(Long aircraftId) throws AircraftNotFoundException, StateException {
        return this.aircraftService.sendTheAircraftToConfirmRegistration(aircraftId);
    }

    @PreAuthorize(value = "hasRole('MAIN_DISPATCHER')")
    @PutMapping(value = "/{aircraftId}/confirm_registration")
    public ResponseEntt confirmAircraftRegistration(Long aircraftId) throws AircraftNotFoundException, StateException {
        return this.aircraftService.confirmAircraftRegistration(aircraftId);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'MAIN_DISPATCHER', 'DISPATCHER', 'MAIN_ENGINEER', 'ENGINEER')")
    @GetMapping(value = "/all_aircrafts")
    public List<AircraftResponseDto> getAllAircraft(
            @RequestParam(required = false)AircraftState aircraftState){
        return this.aircraftService.getAllAircraft(aircraftState);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/get_aircraft")
    public List<AircraftResponseDto> getAircraft(@RequestParam(required = false)LocalDateTime registeredBefore,
                                                 @RequestParam(required = false)LocalDateTime registeredAfter) throws AircraftNotFoundException, IncorrectDate {
        return this.aircraftService.getAircraft(registeredBefore,registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/for_repairs")
    public List<AircraftResponseDto> getAircraftForRepairing(@RequestParam(required = false) LocalDateTime registeredBefore,
                                                             @RequestParam(required = false) LocalDateTime registeredAfter) throws AircraftNotFoundException, IncorrectDate {
        return this.aircraftService.getAircraftForRepairing(registeredBefore,registeredAfter);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ENGINEER')")
    @GetMapping(value = "/for_refuel")
    public List<AircraftResponseDto> getAircraftForRefuel(LocalDateTime registeredBefore,
                                                          LocalDateTime registeredAfter) throws AircraftNotFoundException, IncorrectDate {
        return this.aircraftService.getAircraftForRefuel(registeredBefore,registeredAfter);
    }
}