package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.AircraftState;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class AircraftResponseDto {
    private Long id;
    private String title;
    private AircraftState status;
    private LocalDateTime registeredAt;
}
