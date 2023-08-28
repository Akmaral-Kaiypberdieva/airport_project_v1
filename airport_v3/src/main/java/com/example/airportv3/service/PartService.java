package com.example.airportv3.service;

import com.example.airportv3.dto.PartRequestDto;
import com.example.airportv3.dto.PartResponseDto;
import com.example.airportv3.dto.PartTypeResponseDto;
import com.example.airportv3.entity.InspectionsEntity;
import com.example.airportv3.entity.PartEntity;
import com.example.airportv3.entity.enums.PartState;
import com.example.airportv3.entity.enums.PartType;
import com.example.airportv3.exception.InspectionNotFoundException;
import com.example.airportv3.exception.PartNotFoundException;
import com.example.airportv3.mapper.PartMapper;
import com.example.airportv3.repository.InspectionsRepository;
import com.example.airportv3.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PartService {
    private final InspectionsRepository inspectionsRepository;
    private final PartRepository partRepository;

    public PartResponseDto registerPart(PartRequestDto partRequestDto){
        if (Objects.isNull(partRequestDto)){
            throw new IllegalArgumentException("Part can't be null!");
        }
        PartEntity partEntity = PartMapper.mapPartRequestDtoToEntity(partRequestDto);
        partEntity = this.partRepository.save(partEntity);
        return PartMapper.mapToPartResponseDto(partEntity);
    }

    public List<PartResponseDto> registerParts(List<PartRequestDto> partRequestDtoList){
        if (Objects.isNull(partRequestDtoList)){
            throw new IllegalArgumentException("Parts can't be null!");
        }

        List<PartEntity> partEntities = new ArrayList<>();
        for (PartRequestDto partRequestDto : partRequestDtoList){
            partEntities.add(PartMapper.mapPartRequestDtoToEntity(partRequestDto));
        }
        partEntities = this.partRepository.saveAll(partEntities);
        return PartMapper.mapToPartResponseDtoList(partEntities);
    }

    public List<PartEntity> getPartsById(
            List<Long> partsId
    ) throws PartNotFoundException {
        if(Objects.isNull(partsId) || partsId.isEmpty()) {
            throw new IllegalArgumentException("The list of part id cannot be null or empty!");
        }

        for (Long partId : partsId) {
            if(partId < 1) {
                throw new IllegalArgumentException("The part id cannot be less than 1!");
            }
        }
        List<PartEntity> partEntities = this.partRepository.getPartEntitiesByIdIn(partsId);
        if (partEntities.isEmpty()) {
            throw new PartNotFoundException("Part not found");
        }
        return partEntities;
        }

    public List<InspectionsEntity> getLastAircraftInspectionEntities(Long aircraftId) throws InspectionNotFoundException {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("Aircraft id can't be null!");
        }
        List<InspectionsEntity> lastInspection =
                this.inspectionsRepository.getLastAircraftInspectionByAircraftId(aircraftId);

        if(lastInspection.isEmpty()) {
            throw new InspectionNotFoundException(
                    String.format(
                            "Inspection for aircraft %d not found",
                            aircraftId
                    )
            );
        }
        return lastInspection;
    }

    public PartTypeResponseDto allPartTypes(){
        return PartMapper.mapToPartTypesResponseDto(List.of(PartType.values()));
    }

    public PartState getLastAircraftInspectionResult(Long aircraftId) throws InspectionNotFoundException {
        List<InspectionsEntity> partInspectionsEntityList =
                this.getLastAircraftInspectionEntities(aircraftId);

        for (InspectionsEntity partInspection:
                partInspectionsEntityList) {
            if(partInspection.getPartState().equals(PartState.FIXING)) {
                return PartState.FIXING;
            }
        }
        return PartState.CORRECT;
    }
    }
