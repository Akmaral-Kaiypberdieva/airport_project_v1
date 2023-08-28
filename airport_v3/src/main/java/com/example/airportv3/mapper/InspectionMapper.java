package com.example.airportv3.mapper;

import com.example.airportv3.dto.InspectionRequestDto;
import com.example.airportv3.dto.InspectionResponseDto;
import com.example.airportv3.dto.PartResponseDto;
import com.example.airportv3.dto.StateResponseDto;
import com.example.airportv3.entity.InspectionsEntity;
import com.example.airportv3.entity.enums.PartState;

import java.util.List;
import java.util.stream.Collectors;

public class InspectionMapper {
    public static InspectionsEntity mapInspectionRequestDtoToEntity(InspectionRequestDto inspectionRequestDto){
        return InspectionsEntity.builder()
                .partState(inspectionRequestDto.getPartState())
                .build();
    }

    public static InspectionResponseDto mapToInspectionResponseDto(InspectionsEntity inspections){
        return InspectionResponseDto.builder()
                .id(inspections.getId())
                .partId(inspections.getPartsEntity().getId())
                .partTitle(inspections.getAircraftEntity().getTitle())
                .inspectionCode(inspections.getInspectionCode())
                .registeredAt(inspections.getRegisteredAt())
                .build();
    }

    public static InspectionResponseDto mapInspectionsResponseDto(InspectionsEntity inspections) {
        return InspectionResponseDto.builder()
                .id(inspections.getId())
                .partId(inspections.getPartsEntity().getId())
                .partTitle(inspections.getPartsEntity().getTitle())
                .partState(inspections.getPartState())
                .partType(inspections.getPartsEntity().getPartType())
                .aircraftId(inspections.getAircraftEntity().getId())
                .aircraftTitle(inspections.getAircraftEntity().getTitle())
                .inspectionCode(inspections.getInspectionCode())
                .registeredAt(inspections.getRegisteredAt())
                .build();
    }

    public static List<InspectionResponseDto> mapToInspectionResponseDto(List<InspectionsEntity> inspectionsEntities){
        return inspectionsEntities
                .stream()
                .map(InspectionMapper::mapInspectionsResponseDto)
                .collect(Collectors.toList());
    }

    public static StateResponseDto mapToStateResponseDto(List<PartState> partStates){
        return StateResponseDto.builder()
                .partStates(partStates)
                .build();
    }


}
