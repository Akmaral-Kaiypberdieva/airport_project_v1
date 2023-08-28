package com.example.airportv3.controller;

import com.example.airportv3.dto.InspectionRequestDto;
import com.example.airportv3.dto.InspectionResponseDto;
import com.example.airportv3.exception.*;
import com.example.airportv3.service.AircraftInspectionService;
import com.example.airportv3.service.PartInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aircraft_inspection")
@RequiredArgsConstructor
public class AircraftInspectionController {
    private final PartInspectionService partInspectionService;
    private final AircraftInspectionService aircraftInspectionService;
    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @PutMapping(value = "/assign_aircraft_inspection")
    public String assignAnAircraftInspection(
            @RequestParam Long aircraftId,
            @RequestParam Long engineersId) throws AircraftNotFoundException, EngineerException, UserNotFoundException, RefuelingOfAircraftNoScheduledException {
        return this.aircraftInspectionService.assignAnAircraftInspection(aircraftId,engineersId);
    }
    @PreAuthorize(value = "hasRole('ENGINEER')")
    @PostMapping(value = "/{aircraftId}/inspect")
    public List<InspectionResponseDto> inspectionAircraft(@PathVariable Long aircraftId, @RequestBody List<InspectionRequestDto> inspectionRequestDtoList) throws AircraftNotFoundException, EngineerException, StateException, PartNotFoundException {
        return this.aircraftInspectionService.inspectionAircraft(aircraftId,inspectionRequestDtoList);
    }
    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @GetMapping(value = "/part_inspection_history")
    public List<InspectionResponseDto> partInspectionHistory(
            @RequestParam Long aircraftId,
            @RequestParam(required = false) Long inspectionCode) throws PartInspectionNotFoundException {
        return this.aircraftInspectionService.partInspectionHistory(aircraftId,inspectionCode);
    }

    @PreAuthorize(value = "hasRole('MAIN_ENGINEER')")
    @GetMapping(value = "/last_inspection_history")
    public List<InspectionResponseDto> getLastAircraftInspection(@RequestParam Long aircraftId) throws PartInspectionNotFoundException {
        return this.partInspectionService.getLastAircraftInspection(aircraftId);
    }


}
