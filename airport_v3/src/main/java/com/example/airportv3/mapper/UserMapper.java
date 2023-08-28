package com.example.airportv3.mapper;

import com.example.airportv3.dto.PositionDto;
import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.entity.PositionEntity;
import com.example.airportv3.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserEntity mapUserToDtoEntity(UserRequestDto userRequestDto){
        return UserEntity.builder()
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .build();
    }

    public static UserResponseDto mapToUserResponseDto(UserEntity user){
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .registeredAt(user.getRegisteredAt())
                .positionTitle(user.getUserPosition().getPositionTitle())
                .isEnabled(user.getIsEnabled())
                .build();
    }

    public static PositionDto mapToPositionResponseDto(PositionEntity positionEntity){
        return PositionDto.builder()
                .id(positionEntity.getId())
                .title(positionEntity.getPositionTitle())
                .build();
    }
    public static List<PositionDto> mapToPositionDtoList(
            List<PositionEntity> positionEntities
    ){
        return positionEntities
                .stream()
                .map(UserMapper::mapToPositionResponseDto)
                .collect(Collectors.toList());
    }
}
