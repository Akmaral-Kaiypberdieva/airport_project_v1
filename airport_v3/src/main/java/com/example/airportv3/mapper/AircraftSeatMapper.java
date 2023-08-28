package com.example.airportv3.mapper;

import com.example.airportv3.dto.SeatResponseDto;
import com.example.airportv3.entity.SeatEntity;

public class AircraftSeatMapper {
    public static SeatResponseDto mapToSeatResponseDto(SeatEntity seatEntity){
        return SeatResponseDto.builder()
                .id(seatEntity.getId())
                .rowNumber(seatEntity.getRowNumber())
                .numberInRow(seatEntity.getNumberInRow())
                .isReserved(seatEntity.getIsReserved())
                .build();
    }
}
