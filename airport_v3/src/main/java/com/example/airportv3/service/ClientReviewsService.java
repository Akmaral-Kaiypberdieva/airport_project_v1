package com.example.airportv3.service;

import ch.qos.logback.core.net.server.Client;
import com.example.airportv3.dto.ClientReviewsRequestDto;
import com.example.airportv3.dto.ClientReviewsResponseDto;
import com.example.airportv3.entity.ClientReviewsEntity;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.FlightUserEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.ReviewsException;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.mapper.ReviewsMapper;
import com.example.airportv3.repository.ClientReviewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientReviewsService {
    private final ClientReviewsRepository clientReviewsRepository;
    private final FlightUserService flightUserService;
    private final FlightService flightService;

    public ClientReviewsResponseDto newClientReviews(ClientReviewsRequestDto clientReviewsRequestDto) throws ReviewsException, UserNotFoundException, FlightNotFoundException {
        if(Objects.isNull(clientReviewsRequestDto)) {
            throw new IllegalArgumentException("Reviews can't be null!");
        }
        if(clientReviewsRequestDto.getFeedbackText().isEmpty() || Objects.isNull(clientReviewsRequestDto.getFeedbackText())) {
            throw new ReviewsException("TEXT can't be null or empty!");
        }
        if(Objects.isNull(clientReviewsRequestDto.getFlightRegistrationId())) {
            throw new IllegalArgumentException("Register id can't be null");
        }

        ClientReviewsEntity reviews = ReviewsMapper.mapReviewsRequestDtoToEntity(clientReviewsRequestDto);

        UserEntity userEntity =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FlightUserEntity registration =
                this.flightUserService.getClientFlightRegisteredById(clientReviewsRequestDto.getFlightRegistrationId());

        FlightEntity flight =
                this.flightService.getFlightById(registration.getFlightsEntity().getId());

        reviews.setUsersEntity(userEntity);
        reviews.setFlightsEntity(flight);

        reviews = this.clientReviewsRepository.save(reviews);
        return ReviewsMapper.mapToClientReviewsResponseDto(reviews);
    }
}
