package com.example.airportv3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFlightRequestDto {
    private Long aircraftSeatId;
    private Long flightId;
    private Long userId;
}
