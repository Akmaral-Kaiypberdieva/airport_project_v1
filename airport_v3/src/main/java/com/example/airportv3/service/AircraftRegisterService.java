package com.example.airportv3.service;

import com.example.airportv3.dto.AircraftRegisteredDto;
import com.example.airportv3.dto.AircraftResponseDto;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.PartEntity;
import com.example.airportv3.entity.SeatEntity;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.exception.AircraftIllegalArgumentException;
import com.example.airportv3.exception.PartNotFoundException;
import com.example.airportv3.mapper.AircraftMapper;
import com.example.airportv3.repository.AircraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AircraftRegisterService {
    private final AircraftRepository aircraftRepository;
    private final SeatService seatService;
    private final PartService partService;

    public AircraftResponseDto registerAircraft(
            AircraftRegisteredDto aircraftRegisteredDto
    ) throws AircraftIllegalArgumentException, PartNotFoundException {
        if (Objects.isNull(aircraftRegisteredDto)){
            throw new AircraftIllegalArgumentException("Aircraft can't be null");
        }
        AircraftEntity aircraft = AircraftMapper.mapDtoToEntity(aircraftRegisteredDto);

        List<SeatEntity> aircraftSeatEntities =
                this.seatService.generateSeatInAircraft(
                        aircraftRegisteredDto.getNumberOfRows(),
                        aircraftRegisteredDto.getNumberOfRows()
                );
        aircraft.setAircraftSeatsEntityList(aircraftSeatEntities);

        for (SeatEntity seatEntity : aircraftSeatEntities){
            seatEntity.setAircraftEntity(aircraft);
        }

        List<PartEntity> partEntities = this.partService.getPartsById(aircraftRegisteredDto.getPartIdList());
        aircraft.setPartsEntities(partEntities);
        for (PartEntity part : partEntities){
            part.getAircraftsEntities().add(aircraft);
        }

        aircraft.setStatus(AircraftState.REGISTERED);
        aircraft = this.aircraftRepository.save(aircraft);
        return AircraftMapper.mapToAircraftResponseDto(aircraft);
    }


}
