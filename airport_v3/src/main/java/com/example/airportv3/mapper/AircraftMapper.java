package com.example.airportv3.mapper;


import com.example.airportv3.dto.AircraftRegisteredDto;
import com.example.airportv3.dto.AircraftResponseDto;
import com.example.airportv3.dto.SeatResponseDto;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.SeatEntity;

import java.util.List;

public class AircraftMapper {
    public static AircraftResponseDto mapToAircraftResponseDto(AircraftEntity aircraft) {
        return AircraftResponseDto.builder()
                .id(aircraft.getId())
                .title(aircraft.getTitle())
                .status(aircraft.getStatus())
                .registeredAt(aircraft.getRegisteredAt())
                .build();
    }

    public static AircraftEntity mapDtoToEntity(AircraftRegisteredDto aircraftRegisteredDto){
        return AircraftEntity.builder()
                .title(aircraftRegisteredDto.getTitle())
                .build();
    }

    public static SeatResponseDto mapToSeatResponseDto(SeatEntity seat){
        return SeatResponseDto.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .numberInRow(seat.getNumberInRow())
                .isReserved(seat.getIsReserved())
                .build();
    }
}
