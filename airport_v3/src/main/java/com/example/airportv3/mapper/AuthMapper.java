package com.example.airportv3.mapper;

import com.example.airportv3.dto.JwtResponse;

public class AuthMapper {
    public static JwtResponse mapToResponseDto(String jwtToken){
        return JwtResponse.builder()
                .jwt(jwtToken)
                .build();
    }
}
