package com.example.airportv3.dto;

import com.example.airportv3.entity.enums.UserFlightState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFlightResponseDto {
    private Long id;
    private UserFlightState userStatus;
    private LocalDateTime registeredAt;
    private Long employeeId;
    private String employeeFullName;
    private String employeePositionTitle;
    private Integer seatsRowNumber;
    private Integer seatNumberInRow;
    private Long flightId;
    private String flightDestination;
}
