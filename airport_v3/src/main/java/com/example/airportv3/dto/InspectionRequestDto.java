package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.PartState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionRequestDto {
    private PartState partState;
    private Long partId;
    private Long aircraftId;
}
