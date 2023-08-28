package com.example.airportv3.controller;

import com.example.airportv3.dto.ClientReviewsRequestDto;
import com.example.airportv3.dto.ClientReviewsResponseDto;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.ReviewsException;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.service.ClientReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewsController {
    private final ClientReviewsService clientReviewsService;

    @PreAuthorize(value = "hasRole('CLIENT')")
    @PostMapping(value = "/register")
    public ClientReviewsResponseDto newClientReviews(@RequestBody ClientReviewsRequestDto clientReviewsRequestDto) throws UserNotFoundException, FlightNotFoundException, ReviewsException {
        return this.clientReviewsService.newClientReviews(clientReviewsRequestDto);
    }
}
