package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.PartType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PartTypeResponseDto {
    private List<PartType> partTypeList;
}
