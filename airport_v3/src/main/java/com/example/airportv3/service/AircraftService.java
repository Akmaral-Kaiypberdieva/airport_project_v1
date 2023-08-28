package com.example.airportv3.service;

import com.example.airportv3.dto.AircraftResponseDto;
import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.QAircraftEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.entity.enums.PartState;
import com.example.airportv3.exception.*;
import com.example.airportv3.mapper.AircraftMapper;
import com.example.airportv3.repository.AircraftRepository;
import com.example.airportv3.util.RoleUtil;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.querydsl.core.types.Predicate;


@Service
@RequiredArgsConstructor
public class AircraftService {
    private final PartInspectionService inspectionService;
    private final GetAllFreeMembersService getAllFreeMembersService;
    private final AircraftRepository aircraftRepository;
    public String refuelingTheAircraft(Long aircraftId) throws AircraftNotFoundException, RefuelingOfAircraftNoScheduledException, EngineerException {
        AircraftEntity aircraftEntity = this.findAircraftById(aircraftId);
        if(!aircraftEntity.getStatus().equals(AircraftState.ON_REFUELING)) {
            throw new RefuelingOfAircraftNoScheduledException(
                    String.format("Refueling for the plane with id %d has not been scheduled yet!", aircraftId)
            );
        }

        UserEntity engineer =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!engineer.getId().equals(aircraftEntity.getServicedBy().getId())) {
            throw new EngineerException(
                    String.format(
                            "Refueling the aircraft with id %d was assigned to another engineer!",
                            aircraftEntity.getId()
                    )
            );
        }
        aircraftEntity.getServicedBy().setServicedAircraft(null);
        aircraftEntity.setServicedBy(null);
        aircraftEntity.setStatus(AircraftState.REFUELED);
        aircraftEntity = this.aircraftRepository.save(aircraftEntity);

        return "The plane has been successfully refueled! The current status of the aircraft is" + aircraftEntity.getStatus();
    }

    public String assignTheRefuelingOfTheAircraft(Long aircraftId,Long engineerId) throws AircraftNotFoundException, RefuelingOfAircraftNoScheduledException, UserNotFoundException, EngineerException {
        AircraftEntity aircraft = this.findAircraftById(aircraftId);
        List<FlightEntity> flightEntities = aircraft.getFlightsEntities();

        if(!flightEntities.get(flightEntities.size() - 1).getStatus().equals(FlightState.DEPARTURE_INITIATED)) {
            throw new RefuelingOfAircraftNoScheduledException(
                    "Sending the flight of the aircraft must be initiated"
            );
        }

        UserEntity engineer = this.getAllFreeMembersService.getEngineerEntityById(engineerId);
        if(Objects.nonNull(engineer.getServicedAircraft())) {
            throw new EngineerException(" At the moment, the engineer is servicing another aircraft!");
        }

        aircraft.setStatus(AircraftState.ON_REFUELING);

        engineer.setServicedAircraft(aircraft);
        aircraft.setServicedBy(engineer);

        aircraft = this.aircraftRepository.save(aircraft);
        return
                        String.format(
                                "The plane has been sent for refueling! Current status of the aircraft: %s "
                                , aircraft.getStatus().toString()
                        );
    }

    public AircraftEntity findAircraftById(Long aircraftId) throws AircraftNotFoundException {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("Id can't be null null!");
        }
        if(aircraftId < 1L) {
            throw new IllegalArgumentException("Id aircraft can't be less than 1!");
        }

        Optional<AircraftEntity> aircraftEntityOptional =
                this.aircraftRepository.getAircraftEntityById(aircraftId);
        if(aircraftEntityOptional.isEmpty()) {
            throw new AircraftNotFoundException(String.format("No aircraft with ID %d found!", aircraftId));
        }
        return aircraftEntityOptional.get();
    }

    public ResponseEntt confirmTheServiceabilityOfTheAircraft(Long aircraftId) throws AircraftNotFoundException, StateException, PartInspectionNotFoundException {
        AircraftEntity aircraft = this.findAircraftById(aircraftId);

        if(!aircraft.getStatus().equals(AircraftState.INSPECTED)) {
            throw new StateException(
                    "To confirm the serviceability of the aircraft, the aircraft must be inspected by an engineer!"
            );
        }

        if(!this.inspectionService.getLastAircraftInspectionResult(aircraftId).equals(PartState.CORRECT)) {
            throw new StateException(
                    String.format(
                            "All parts of the aircraft must be serviceable" +
                                    "RESULT: %s",
                            PartState.FIXING.toString()
                    )
            );
        }

        aircraft.setStatus(AircraftState.SERVICEABLE);

        aircraft = this.aircraftRepository.save(aircraft);
        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody(
                        String.format(
                                "The serviceability of the aircraft has been confirmed! RESULT: %s",
                                aircraft.getStatus().toString()
                        )
                );
    }

    public ResponseEntt sendTheAircraftToConfirmRegistration(Long aircraftId) throws AircraftNotFoundException, StateException {
        AircraftEntity aircraft = this.findAircraftById(aircraftId);
        if (!aircraft.getStatus().equals(AircraftState.SERVICEABLE)){
            throw new StateException("the inspection must be confirmed by the chief engineer!");
        }

        aircraft.setStatus(AircraftState.REGISTRATION_PENDING_CONFIRMATION);
        aircraft = this.aircraftRepository.save(aircraft);
        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody(
                        String.format(
                                "The plane has been sent for registration confirmation! RESULT: %s"
                                , aircraft.getStatus().toString()
                        )
                );
    }

    public ResponseEntt confirmAircraftRegistration(Long aircraftId) throws AircraftNotFoundException, StateException {
        AircraftEntity aircraft = this.findAircraftById(aircraftId);
        if(!aircraft.getStatus().equals(AircraftState.REGISTRATION_PENDING_CONFIRMATION)) {
            throw new StateException(
                    "To confirm the registration of the aircraft, it must be sent to the main dispatcher by the dispatcher"
            );
        }
        aircraft.setStatus(AircraftState.AVAILABLE);

        aircraft = this.aircraftRepository.save(aircraft);
        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody(
                        String.format(
                                "The plane has been sent for registration confirmation! RESULT: %s",
                                aircraft.getStatus().toString()
                        )
                );
    }


    public List<AircraftResponseDto> getAllAircraft(AircraftState aircraftState){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QAircraftEntity root = QAircraftEntity.aircraftEntity;

        if (Objects.nonNull(aircraftState)){
            booleanBuilder.and(root.status.eq(aircraftState));
        }
        return (List<AircraftResponseDto>) booleanBuilder.getValue();
    }

    public List<AircraftResponseDto> getAircraft(LocalDateTime registeredBefore,
                                                 LocalDateTime registeredAfter) throws IncorrectDate, AircraftNotFoundException {
        UserEntity userAuthorized =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication();
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.predicateForSearchAircraft(registeredAfter,registeredBefore)
        );
        QAircraftEntity root = QAircraftEntity.aircraftEntity;
        booleanBuilder.and(root.status.eq(AircraftState.REGISTERED));

        if (RoleUtil.checkWhetherTheListOfUserRolesContainsSuchUserRoleName(
                userAuthorized.getRoles(),
                "ENGINEER"
        )){
            booleanBuilder.and(root.servicedBy.id.eq(userAuthorized.getId()));
        }
        return this.findPredicateAircraft(booleanBuilder.getValue());
    }

    public List<AircraftResponseDto> findPredicateAircraft(Predicate predicate) throws AircraftNotFoundException {
        Iterable<AircraftEntity> aircraftsEntityIterable =
                this.aircraftRepository.findAll((Sort) predicate);
        List<AircraftResponseDto> aircraftResponseDtoList =
                StreamSupport
                        .stream(aircraftsEntityIterable.spliterator(), false)
                        .map(AircraftMapper::mapToAircraftResponseDto)
                        .collect(Collectors.toList());

        if(aircraftResponseDtoList.isEmpty()) {
            throw new AircraftNotFoundException("Aircraft not found!");
        }
        return aircraftResponseDtoList;
    }
    public Predicate predicateForSearchAircraft(LocalDateTime registeredAfter,
                                                LocalDateTime registeredBefore) throws IncorrectDate {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QAircraftEntity root = QAircraftEntity.aircraftEntity;

        if (Objects.nonNull(registeredAfter)){
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }
        if (Objects.nonNull(registeredBefore)){
            if (Objects.nonNull(registeredAfter) && registeredAfter.isAfter(registeredBefore)){
                throw new IncorrectDate("The start date cannot be later than the end date");
            }
            booleanBuilder.and(root.registeredAt.goe(registeredAfter));
        }
        return booleanBuilder.getValue();
    }

    public List<AircraftResponseDto> getAircraftForRepairing( LocalDateTime registeredBefore,
                                                              LocalDateTime registeredAfter) throws IncorrectDate, AircraftNotFoundException {
        UserEntity userAuthorized =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication();
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.predicateForSearchAircraft(registeredAfter,registeredBefore)
        );
        QAircraftEntity root = QAircraftEntity.aircraftEntity;
        booleanBuilder.and(root.status.eq(AircraftState.ON_REPAIRS));

        if (RoleUtil.checkWhetherTheListOfUserRolesContainsSuchUserRoleName(
                userAuthorized.getRoles(),
                "ENGINEER"
        )){
            booleanBuilder.and(root.servicedBy.id.eq(userAuthorized.getId()));
        }
        return this.findPredicateAircraft(booleanBuilder.getValue());
    }

    public List<AircraftResponseDto> getAircraftForRefuel(LocalDateTime registeredBefore,
                                                          LocalDateTime registeredAfter) throws IncorrectDate, AircraftNotFoundException {
        UserEntity userAuthorized =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication();
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.predicateForSearchAircraft(registeredAfter,registeredBefore)
        );
        QAircraftEntity root = QAircraftEntity.aircraftEntity;
        booleanBuilder.and(root.status.eq(AircraftState.ON_REFUELING));

        if (RoleUtil.checkWhetherTheListOfUserRolesContainsSuchUserRoleName(
                userAuthorized.getRoles(),
                "ENGINEER"
        )){
            booleanBuilder.and(root.servicedBy.id.eq(userAuthorized.getId()));
        }
        return this.findPredicateAircraft(booleanBuilder.getValue());
    }
}
