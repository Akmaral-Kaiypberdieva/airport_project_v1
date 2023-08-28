package com.example.airportv3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientReviewsResponseDto {
    private Long id;
    private String feedbackText;
    private LocalDateTime registeredAt;
    private Long clientId;
    private Long flightId;
}
