package com.example.airportv3.service;

import com.example.airportv3.dto.UserFlightRequestDto;
import com.example.airportv3.dto.UserFlightResponseDto;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.FlightUserEntity;
import com.example.airportv3.entity.SeatEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.entity.enums.UserFlightState;
import com.example.airportv3.exception.*;
import com.example.airportv3.mapper.FlightMapper;
import com.example.airportv3.repository.FlightUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.font.TextHitInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFlightRegisterService {
    private final FlightUserRepository flightUserRepository;
    private final SeatService seatService;
    private final FlightService flightService;
    private final FlightUserService flightUserService;
    private final UserService userService;
    public UserFlightResponseDto registerClientForFlight(UserFlightRequestDto userFlightRequestDto) throws FlightNotFoundException, FlightException, SeatReservedException, SeatNotFoundException {
        if(Objects.isNull(userFlightRequestDto)) {
            throw new IllegalArgumentException("Registration can't be null!");
        }
        if(Objects.isNull(userFlightRequestDto.getFlightId()) || Objects.isNull(userFlightRequestDto.getAircraftSeatId())) {
            throw new IllegalArgumentException(
                    "Flight id can't be null"
            );
        }

        UserEntity client =
                (UserEntity) SecurityContextHolder.getContext().getAuthentication();

        FlightEntity flightEntity = this.flightService.getFlightById(userFlightRequestDto.getFlightId());
        if (!flightEntity.getStatus().equals(FlightState.SELLING_TICKETS)){
            throw new FlightException("There are no tickets for this ray");
        }
        SeatEntity seatEntity = this.seatService.reserveSeat(userFlightRequestDto.getAircraftSeatId());

        flightEntity = this.flightService.updateNumberOfTickets(flightEntity.getId());

        FlightUserEntity flightUserEntity = FlightUserEntity.builder()
                .flightsEntity(flightEntity)
                .aircraftSeatsEntity(seatEntity)
                .usersEntity(client)
                .userStatus(UserFlightState.CLIENT_REGISTERED_FOR_FLIGHT)
                .build();

        this.flightUserRepository.save(flightUserEntity);
        return FlightMapper.mapUserFlightRegisterResponseToDto(flightUserEntity);
    }

    public UserFlightResponseDto cancelRegistered(Long registrationId) throws FlightNotFoundException, CancelRegistrationException, SeatReservedException, SeatNotFoundException {
        UserEntity client = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        FlightUserEntity registrationClient =
                this.flightUserService.getClientRegistrationOnFlightByFlightIdAndClientId(registrationId,client.getId());

        if (!registrationClient.getFlightsEntity().getStatus().equals(FlightState.SELLING_TICKETS)){
            throw new CancelRegistrationException("It is possible to cancel registration only during ticket sales!");
        }
        this.seatService.cancellationOfSeatReservation(registrationClient.getFlightsEntity().getId());
        this.flightService.updateNumberOfTickets(registrationClient.getFlightsEntity().getId());

        registrationClient.setUserStatus(UserFlightState.CLIENT_REGISTRATION_DECLINED);

        registrationClient = this.flightUserRepository.save(registrationClient);
        return FlightMapper.mapUserFlightRegisterResponseToDto(registrationClient);
    }

    public List<UserFlightResponseDto> registerMembersForFlight(List<UserFlightRequestDto> flightRequestDtoList) throws FlightNotFoundException, FlightException, UserNotFoundException {
        if(Objects.isNull(flightRequestDtoList) || flightRequestDtoList.isEmpty()) {
            throw new IllegalArgumentException(
                    "The list of checked-in employees cannot be null or empty!"
            );
        }

        Long flightId = flightRequestDtoList.get(0).getFlightId();
        List<Long> crewMembersIdList = new ArrayList<>();

        for (UserFlightRequestDto requestDto : flightRequestDtoList){
            if(Objects.isNull(requestDto.getFlightId()) || Objects.isNull(requestDto.getUserId())) {
                throw new IllegalArgumentException(
                        "Employee id and flight id can't be null!"
                );
            }
            if(!flightId.equals(requestDto.getFlightId())){
                throw new IllegalArgumentException(
                        "The flight ID for all employees registered for this flight must match"
                );
            }
            crewMembersIdList.add(requestDto.getUserId());
        }

        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.REGISTERED)){
            throw new FlightException("Flight not registered");
        }

        List<UserEntity> crewMembers =
                this.userService.userEntitiesByIdList(crewMembersIdList);
        flightEntity.setStatus(FlightState.CREW_MEMBERS_REGISTERED);
        List<FlightUserEntity> crewMembersRegistration = new ArrayList<>();
        for (UserEntity crewMember : crewMembers){
            FlightUserEntity crewMemberRegistration = new FlightUserEntity();
            crewMemberRegistration.setUsersEntity(crewMember);

            crewMemberRegistration.setFlightsEntity(flightEntity);
            flightEntity.getUserFlightsEntities().add(crewMemberRegistration);

            crewMemberRegistration.setUserStatus(UserFlightState.CREW_MEMBER_REGISTERED_FOR_FLIGHT);

            crewMembersRegistration.add(crewMemberRegistration);
        }
        crewMembersRegistration = this.flightUserRepository.saveAll(crewMembersRegistration);
        return crewMembersRegistration
                .stream()
                .map(FlightMapper::mapUserFlightRegisterResponseToDto)
                .collect(Collectors.toList());
    }

}
