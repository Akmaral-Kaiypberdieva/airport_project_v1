package com.example.airportv3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftRegisteredDto {
    @NotBlank(message = "The title name can't be empty")
    private String title;
    @NotBlank(message = "The number of seats can't be empty")
    private Integer numberOfSeats;
    @NotBlank(message = "The number of seats can't be empty")
    private Integer numberOfRows;
    private List<Long> partIdList;
}
