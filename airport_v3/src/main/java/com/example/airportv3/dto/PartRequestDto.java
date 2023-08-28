package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartRequestDto {
    private String title;
    private PartType partType;
}
