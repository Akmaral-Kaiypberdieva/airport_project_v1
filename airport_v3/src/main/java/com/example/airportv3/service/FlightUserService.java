package com.example.airportv3.service;

import com.example.airportv3.dto.UserFlightResponseDto;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.FlightUserEntity;
import com.example.airportv3.entity.QFlightUserEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.entity.enums.UserFlightState;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.StateException;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.mapper.FlightMapper;
import com.example.airportv3.repository.FlightUserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service

public class FlightUserService {
    private final FlightUserRepository flightUserRepository;

    @Autowired
    public FlightUserService(FlightUserRepository flightUserRepository) {
        this.flightUserRepository = flightUserRepository;
    }

    public UserFlightResponseDto checkClient(Long clientRegisterId) throws UserNotFoundException, StateException {
        FlightUserEntity registerUser = this.getClientFlightRegisteredById(clientRegisterId);
        FlightEntity flight = registerUser.getFlightsEntity();
        if(!flight.getStatus().equals(FlightState.CLIENT_CHECK)) {
            throw new StateException(
                    "Check of places should be assigned by the dispatcher"
            );
        }
        if(!registerUser.getUserStatus().equals(UserFlightState.CLIENT_REGISTERED_FOR_FLIGHT)) {
            throw new StateException(
                    "The customer must be registered for the flight"
            );
        }

        registerUser.setUserStatus(UserFlightState.CLIENT_CHECKED);

        registerUser = this.flightUserRepository.save(registerUser);
        return FlightMapper.mapUserFlightRegisterResponseToDto(registerUser);
    }


    public FlightUserEntity getClientRegistrationOnFlightByFlightIdAndClientId(Long clientRegisterId,Long userId) throws FlightNotFoundException {
        if(Objects.isNull(clientRegisterId)) {
            throw new IllegalArgumentException("Registered id can't be null!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightUserEntity root = QFlightUserEntity.flightUserEntity;

        booleanBuilder.and(root.userStatus.eq(UserFlightState.CLIENT_REGISTERED_FOR_FLIGHT));
        booleanBuilder.and(root.id.eq(clientRegisterId));
        booleanBuilder.and(root.usersEntity.id.eq(userId));

        Optional<FlightUserEntity> flightUserEntityOptional =
                this.flightUserRepository.findOne(booleanBuilder.getValue());

        if (flightUserEntityOptional.isEmpty()){
            throw new FlightNotFoundException("Client register not found");
        }
        return flightUserEntityOptional.get();
    }

    public FlightUserEntity getClientFlightRegisteredById(Long registrationId) throws UserNotFoundException {
        if(Objects.isNull(registrationId)) {
            throw new IllegalArgumentException("Flight id can't be null!");
        }

        Optional<FlightUserEntity> flightUserEntityOptional =
                this.flightUserRepository.getFlightUserEntityById(registrationId);

        if(flightUserEntityOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return flightUserEntityOptional.get();
    }

    public UserFlightResponseDto destinationsFoodDistribution(Long clientsRegisterId) throws UserNotFoundException, StateException {

        FlightUserEntity registerClient = this.getClientFlightRegisteredById(clientsRegisterId);
        FlightEntity flight = registerClient.getFlightsEntity();
        if(!flight.getStatus().equals(FlightState.FLIGHT_FOOD_DISTRIBUTION)) {
            throw new StateException(
                    "The distribution of food must be appointed by the chief steward"
            );
        }
        if(!registerClient.getUserStatus().equals(UserFlightState.CLIENT_BRIEFED)) {
            throw new StateException(
                    "The client should be marked as instructed");
        }

        registerClient.setUserStatus(UserFlightState .CLIENT_FOOD_DISTRIBUTED);

        registerClient = this.flightUserRepository.save(registerClient);
        return FlightMapper.mapUserFlightRegisterResponseToDto(registerClient);
    }

    public List<UserFlightResponseDto> getAllUserRegistrations(Long flightId,UserFlightState state,
                                                              Long userId,
                                                              Boolean isClients) throws UserNotFoundException {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightUserEntity root = QFlightUserEntity.flightUserEntity;


        if(Objects.nonNull(flightId)) {
            booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        }
        if(Objects.nonNull(state)) {
            booleanBuilder.and(root.userStatus.eq(state));
        }
        if(Objects.nonNull(userId)) {

            booleanBuilder.and(root.usersEntity.id.eq(userId));
        }
        if(Objects.nonNull(isClients)) {
            if(isClients) {
                booleanBuilder.and(root.usersEntity.userPosition.positionTitle.eq("CLIENT"));
            } else {
                booleanBuilder.and(root.usersEntity.userPosition.positionTitle.ne("CLIENT"));
            }
        }

        Iterable<FlightUserEntity> flightUserEntityIterable =
                this.flightUserRepository.findAll(booleanBuilder.getValue());
        List<UserFlightResponseDto> flightResponseDtoList =
                StreamSupport
                        .stream(flightUserEntityIterable.spliterator(), false)
                        .map(FlightMapper::mapUserFlightRegisterResponseToDto)
                        .collect(Collectors.toList());
        if(flightResponseDtoList.isEmpty()) {
            throw new UserNotFoundException(
                    "Registration is not found!"
            );
        }
        return flightResponseDtoList;
    }

    public List<UserFlightResponseDto> getAllEmployeesRegistrations(
            Long flightId,
            UserFlightState state
    ) throws UserNotFoundException {
        return this.getAllUserRegistrations(flightId, state, null, Boolean.FALSE);
    }


    public List<UserFlightResponseDto> getAllClientRegistrations(
            Long flightId,
            UserFlightState state
    ) throws UserNotFoundException {
        return this.getAllUserRegistrations(flightId, state, null, Boolean.TRUE);
    }

    public UserFlightResponseDto currentFlight() throws UserNotFoundException {
        UserEntity userEntity =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return FlightMapper.mapUserFlightRegisterResponseToDto(this.getClientFlightRegisteredById(userEntity.getId()));
    }
    public UserFlightResponseDto instructClients(Long clientsRegisterId) throws UserNotFoundException, StateException {
        FlightUserEntity registerClient = this.getClientFlightRegisteredById(clientsRegisterId);
        FlightEntity flight = registerClient.getFlightsEntity();
        if(!flight.getStatus().equals(FlightState.CLIENTS_BRIEFING)) {
            throw new StateException(
                    "The instruct must be appointed by the chief steward"
            );
        }
        registerClient.setUserStatus(UserFlightState.CLIENT_BRIEFED);

        registerClient = this.flightUserRepository.save(registerClient);
        return FlightMapper.mapUserFlightRegisterResponseToDto(registerClient);
    }
    public boolean checkIfAllPassengersOfFlightHaveStatus(
            Long flightId,
            UserFlightState state
    ) {
        if(Objects.isNull(flightId) || Objects.isNull(state)) {
            throw new IllegalArgumentException(
                    "Flight id and state can't be null"
            );
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightUserEntity root = QFlightUserEntity.flightUserEntity;
        booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        booleanBuilder.and(root.usersEntity.userPosition.positionTitle.eq("CLIENT"));

        Iterable<FlightUserEntity> flightUserEntityIterable =
                this.flightUserRepository.findAll(booleanBuilder.getValue());
        List<FlightUserEntity> flightUserEntityList =
                StreamSupport
                        .stream(flightUserEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        for (FlightUserEntity registerUser : flightUserEntityList) {
            if(!registerUser.getUserStatus().equals(state)) {
                return false;
            }
        }
        return true;
    }

    public UserFlightResponseDto confirmFlightReadiness() throws UserNotFoundException, StateException {
        UserEntity userEntity =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FlightUserEntity flightUserEntity = this.getClientFlightRegisteredById(userEntity.getId());

        if (!flightUserEntity.getFlightsEntity().getStatus().equals(FlightState.CREW_MEMBERS_REGISTERED)){
            throw new StateException("The crew readiness check can begin only after the customer readiness check");
        }
        flightUserEntity.setUserStatus(UserFlightState.CREW_MEMBER_READY);
        this.checkIfAllCrewMembersReadyForFlight(flightUserEntity.getId());

        flightUserEntity = this.flightUserRepository.save(flightUserEntity);
        return FlightMapper.mapUserFlightRegisterResponseToDto(flightUserEntity);
    }

    public boolean checkIfAllCrewMembersReadyForFlight(Long flightId){
        if(Objects.isNull(flightId)) {
            throw new IllegalArgumentException(
                    "Flight id can't be null!"
            );
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightUserEntity root = QFlightUserEntity.flightUserEntity;

        booleanBuilder.and(root.flightsEntity.id.eq(flightId));
        booleanBuilder.and(root.usersEntity.userPosition.positionTitle.ne("CLIENT"));

        Iterable<FlightUserEntity> flightUserEntityIterable =
                this.flightUserRepository.findAll(booleanBuilder.getValue());

        List<FlightUserEntity> flightUserEntityList =
                StreamSupport
                        .stream(flightUserEntityIterable.spliterator(),false)
                        .collect(Collectors.toList());

        for (FlightUserEntity registerUser : flightUserEntityList){
            if (!registerUser.getUserStatus().equals(UserFlightState.CREW_MEMBER_READY)){
                return false;
            }
        }
        return true;
    }
 }
