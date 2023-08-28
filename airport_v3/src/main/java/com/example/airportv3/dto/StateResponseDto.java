package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.PartState;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StateResponseDto {
   private List<PartState> partStates;

}
