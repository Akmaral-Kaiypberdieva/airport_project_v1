package com.example.airportv3.mapper;

import com.example.airportv3.dto.PartRequestDto;
import com.example.airportv3.dto.PartResponseDto;
import com.example.airportv3.dto.PartTypeResponseDto;
import com.example.airportv3.entity.PartEntity;
import com.example.airportv3.entity.enums.PartType;

import java.util.List;
import java.util.stream.Collectors;

public class PartMapper {
    public static PartEntity mapPartRequestDtoToEntity(PartRequestDto partRequestDto) {
        return PartEntity.builder()
                .title(partRequestDto.getTitle())
                .partType(partRequestDto.getPartType())
                .build();
    }

    public static PartResponseDto mapToPartResponseDto(PartEntity part) {
        return PartResponseDto.builder()
                .id(part.getId())
                .title(part.getTitle())
                .partType(part.getPartType())
                .registeredAt(part.getRegisteredAt())
                .build();
    }

    public static List<PartResponseDto> mapToPartResponseDtoList(List<PartEntity> partEntities) {
        return partEntities
                .stream()
                .map(PartMapper::mapToPartResponseDto)
                .collect(Collectors.toList());
    }

    public static PartTypeResponseDto mapToPartTypesResponseDto(List<PartType> partTypeList) {
        return PartTypeResponseDto.builder().partTypeList(partTypeList).build();
    }
}
