package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.FlightState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightResponseDto {
    private Long id;
    private String destination;
    private FlightState status;
    private Integer ticketsLeft;
    private LocalDateTime registeredAt;
}
