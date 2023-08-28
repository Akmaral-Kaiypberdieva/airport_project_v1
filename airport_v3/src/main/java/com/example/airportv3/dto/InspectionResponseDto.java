package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.PartState;
import com.example.airportv3.entity.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionResponseDto {
    private Long id;
    private Long inspectionCode;
    private PartState partState;
    private LocalDateTime registeredAt;
    private Long partId;
    private PartType partType;
    private String partTitle;
    private Long aircraftId;
    private String aircraftTitle;
}
