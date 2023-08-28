package com.example.airportv3.mapper;

import com.example.airportv3.dto.FlightRequestDto;
import com.example.airportv3.dto.FlightResponseDto;
import com.example.airportv3.dto.UserFlightRequestDto;
import com.example.airportv3.dto.UserFlightResponseDto;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.FlightUserEntity;

public class FlightMapper {
    public static FlightEntity mapFlightRequestDtoToEntity(FlightRequestDto flightRequestDto){
        return FlightEntity.builder()
                .destination(flightRequestDto.getDestination())
                .build();
    }

    public static FlightResponseDto mapToFlightResponseDto(FlightEntity flightEntity){
        return FlightResponseDto.builder()
                .id(flightEntity.getId())
                .destination(flightEntity.getDestination())
                .registeredAt(flightEntity.getRegisteredAt())
                .status(flightEntity.getStatus())
                .ticketsLeft(flightEntity.getTicketsLeft())
                .build();
    }

    public static UserFlightResponseDto mapUserFlightRegisterResponseToDto(FlightUserEntity flightUserEntity){
        UserFlightResponseDto responseDto =
                 UserFlightResponseDto.builder()
                         .id(flightUserEntity.getId())
                         .flightId(flightUserEntity.getFlightsEntity().getId())
                         .flightDestination(flightUserEntity.getFlightsEntity().getDestination())
                         .employeeId(flightUserEntity.getId())
                         .employeeFullName(flightUserEntity.getUsersEntity().getUsername())
                         .employeePositionTitle(flightUserEntity.getUsersEntity().getUserPosition().getPositionTitle())
                         .registeredAt(flightUserEntity.getRegisteredAt())
                         .userStatus(flightUserEntity.getUserStatus())
                         .build();
        responseDto.setSeatsRowNumber(flightUserEntity.getAircraftSeatsEntity().getRowNumber());
        responseDto.setSeatNumberInRow(flightUserEntity.getAircraftSeatsEntity().getNumberInRow());
        return responseDto;
    }
}
