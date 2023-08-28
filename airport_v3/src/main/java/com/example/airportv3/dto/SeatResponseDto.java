package com.example.airportv3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponseDto {
    private Long id;
    private Integer numberInRow;
    private Integer rowNumber;
    private Boolean isReserved;
}
