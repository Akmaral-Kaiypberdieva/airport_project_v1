package com.example.airportv3.service;

import com.example.airportv3.dto.InspectionRequestDto;
import com.example.airportv3.dto.InspectionResponseDto;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.InspectionsEntity;
import com.example.airportv3.entity.PartEntity;
import com.example.airportv3.entity.enums.PartState;
import com.example.airportv3.exception.AircraftNotFoundException;
import com.example.airportv3.exception.EngineerException;
import com.example.airportv3.exception.PartInspectionNotFoundException;
import com.example.airportv3.exception.PartNotFoundException;
import com.example.airportv3.mapper.InspectionMapper;
import com.example.airportv3.repository.InspectionsRepository;
import com.example.airportv3.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartInspectionService {
    private final PartService partService;

    private final InspectionsRepository inspectionsRepository;

    private Long currentMaxInspectionCode;

    @PostConstruct
    private void init() {
        this.currentMaxInspectionCode = this.inspectionsRepository.getCurrentMaxInspectionsCode();
        if(Objects.isNull(this.currentMaxInspectionCode)) {
            this.currentMaxInspectionCode = 1L;
        }
    }

    public List<InspectionResponseDto> registerPartInspection(
            AircraftEntity aircraft,
            List<InspectionRequestDto> requestDtoList
    ) throws AircraftNotFoundException, PartNotFoundException, EngineerException {
        if(Objects.isNull(requestDtoList)) {
            throw new IllegalArgumentException("Parts can't be null!");
        }

        List<InspectionsEntity> inspectionsEntities = new ArrayList<>();
        List<Long> partIdList = new ArrayList<>();
        Long aircraftId = requestDtoList.get(0).getAircraftId();

        for (InspectionRequestDto requestDto : requestDtoList) {
            Long requestDtoAircraftId = requestDto.getAircraftId();
            if (!aircraftId.equals(requestDtoAircraftId)) {
                throw new IllegalArgumentException(
                        "All parts must be the same"
                );
            }
            inspectionsEntities.add(InspectionMapper.mapInspectionRequestDtoToEntity(requestDto));
            partIdList.add(requestDto.getPartId());
        }
        if(!aircraft.getId().equals(aircraftId)) {
            throw new AircraftNotFoundException(
                    String.format("The service was assigned to another aircraft with id %d", aircraftId)
            );
        }

        List<PartEntity> partsEntities =
                this.partService.getPartsById(partIdList);

        if(Objects.isNull(aircraft.getServicedBy())){
            throw new EngineerException(
                    String.format(
                            "Not a single engineer was appointed for aircraft with id: %d!",
                            aircraftId
                    )
            );
        }

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        for (int i = 0; i < partsEntities.size(); i++) {
            PartEntity part = partsEntities.get(i);

            inspectionsEntities.get(i)
                    .setPartsEntity(part)
                    .setRegisteredAt(localDateTimeNow)
                    .setAircraftEntity(aircraft)
                    .setConductedBy(aircraft.getServicedBy());
        }

        this.currentMaxInspectionCode += 1L;
        for (InspectionsEntity inspection : inspectionsEntities) {
            inspection.setInspectionCode(this.currentMaxInspectionCode);
        }

        inspectionsEntities = this.inspectionsRepository.saveAll(inspectionsEntities);
        return InspectionMapper.mapToInspectionResponseDto(inspectionsEntities);
    }
    public List<InspectionsEntity> getLastAircraftInspectionEntities(Long aircraftId)throws PartInspectionNotFoundException {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("Aircraft id can't be null!");
        }

        List<InspectionsEntity> lastInspection =
                this.inspectionsRepository.getLastAircraftInspectionByAircraftId(aircraftId);

        if(lastInspection.isEmpty()) {
            throw new PartInspectionNotFoundException(
                    String.format(
                            "Inspections for this aircraft not found %d",
                            aircraftId
                    )
            );
        }
        return lastInspection;
    }

    public PartState getLastAircraftInspectionResult(Long aircraftId) throws PartInspectionNotFoundException {
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
    public List<InspectionResponseDto> getLastAircraftInspection(Long aircraftId) throws PartInspectionNotFoundException {
        return this.getLastAircraftInspectionEntities(aircraftId)
                .stream()
                .map(InspectionMapper::mapToInspectionResponseDto)
                .collect(Collectors.toList());
    }

}