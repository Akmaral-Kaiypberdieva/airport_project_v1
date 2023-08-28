package com.example.airportv3.service;

import com.example.airportv3.dto.FlightRequestDto;
import com.example.airportv3.dto.FlightResponseDto;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.exception.AircraftException;
import com.example.airportv3.exception.AircraftNotFoundException;
import com.example.airportv3.mapper.FlightMapper;
import com.example.airportv3.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlightRegisterService {
    private final GetAllFreeMembersService getAllFreeMembersService;
    private final FlightRepository flightRepository;
    private final SeatService seatService;
    private final AircraftService aircraftService;

    public FlightResponseDto registerFlight(FlightRequestDto flightRequestDto) throws AircraftNotFoundException, AircraftException {
        if (Objects.isNull(flightRequestDto)){
            throw new IllegalArgumentException("Flight can' be null");
        }
        FlightEntity flightEntity = FlightMapper.mapFlightRequestDtoToEntity(flightRequestDto);

        AircraftEntity aircraft = this.aircraftService.findAircraftById(flightRequestDto.getAircraftId());
        if (!aircraft.getStatus().equals(AircraftState.AVAILABLE)){
            throw new AircraftException("It is necessary to choose an available aircraft");
        }
        flightEntity.setAircraftEntity(aircraft);
        Integer seatNumber = this.seatService.numberOfFreeSeatsByAircraftId(flightRequestDto.getAircraftId());

        flightEntity.setStatus(FlightState.REGISTERED);
        flightEntity.setTicketsLeft(seatNumber);
        flightEntity = this.flightRepository.save(flightEntity);
        return FlightMapper.mapToFlightResponseDto(flightEntity);
    }


}
