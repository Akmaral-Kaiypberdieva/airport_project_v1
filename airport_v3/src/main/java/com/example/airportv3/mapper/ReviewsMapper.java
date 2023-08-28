package com.example.airportv3.mapper;

import com.example.airportv3.dto.ClientReviewsRequestDto;
import com.example.airportv3.dto.ClientReviewsResponseDto;
import com.example.airportv3.entity.ClientReviewsEntity;

public class ReviewsMapper {
    public static ClientReviewsEntity mapReviewsRequestDtoToEntity(ClientReviewsRequestDto reviewsRequestDto){
        return ClientReviewsEntity.builder()
                .feedbackText(reviewsRequestDto.getFeedbackText())
                .build();
    }

    public static ClientReviewsResponseDto mapToClientReviewsResponseDto(ClientReviewsEntity clientReviewsEntity){
        return ClientReviewsResponseDto.builder()
                .id(clientReviewsEntity.getId())
                .clientId(clientReviewsEntity.getUsersEntity().getId())
                .flightId(clientReviewsEntity.getFlightsEntity().getId())
                .feedbackText(clientReviewsEntity.getFeedbackText())
                .registeredAt(clientReviewsEntity.getRegisteredAt())
                .build();
    }
}
