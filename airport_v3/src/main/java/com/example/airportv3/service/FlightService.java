package com.example.airportv3.service;

import com.example.airportv3.dto.FlightResponseDto;
import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.FlightUserEntity;
import com.example.airportv3.entity.QFlightEntity;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.entity.enums.UserFlightState;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.StateException;
import com.example.airportv3.mapper.FlightMapper;
import com.example.airportv3.repository.FlightRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final SeatService seatService;


     public FlightEntity updateNumberOfTickets(Long flightId) throws FlightNotFoundException {
         FlightEntity flightEntity = this.getFlightById(flightId);

         Integer freeSeats = this.seatService.getFreeSeatsInAircraftById(flightEntity.getId());

         if (freeSeats.equals(0)){
             flightEntity.setStatus(FlightState.SOLD);
     }
         flightEntity.setTicketsLeft(freeSeats);
         return this.flightRepository.save(flightEntity);
     }

     public FlightEntity getFlightById(Long flightId) throws FlightNotFoundException {
         if (Objects.isNull(flightId)) {
             throw new IllegalArgumentException("Flight id can't be null!");
         }
         Optional<FlightEntity> flightEntityOptional = this.flightRepository.getFlightsEntityById(flightId);
         if (flightEntityOptional.isEmpty()) {
             throw new FlightNotFoundException("Flight not found!");
         }
         return flightEntityOptional.get();
     }

    public ResponseEntt confirmationForRefuelingTheAircraft(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.DEPARTURE_INITIATED)){
            throw new StateException("Initialize sending the flight");
        }
        flightEntity.setStatus(FlightState.TECH_PREP_COMPLETE);
        this.flightRepository.save(flightEntity);
        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Refueling of the aircraft has been carried out");
    }

    public ResponseEntt assignBriefing(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);
        flightEntity.setStatus(FlightState.CLIENTS_BRIEFING);
        this.flightRepository.save(flightEntity);
        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Customer readiness check started");
    }

    public ResponseEntt initiateFlightStart(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.CREW_READY)){
            throw new StateException("Non-crew members have confirmed their readiness");
        }
        flightEntity.setStatus(FlightState.CLIENTS_BRIEFING);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Flight start initiated");
    }

    public ResponseEntt confirmDepartureFlight(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.DEPARTURE_CONFIRMED)){
            throw new StateException("the flight start must be initiated by the dispatcher");
        }
        flightEntity.setStatus(FlightState.FLIGHT_STARTED);
        flightEntity.getAircraftEntity().setStatus(AircraftState.IN_AIR);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Flight start");
    }

    public ResponseEntt destinationsFoodDistribution(Long flightId) throws StateException, FlightNotFoundException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.FLIGHT_STARTED)){
            throw new StateException("The flight must be started");
        }
        flightEntity.setStatus(FlightState.FLIGHT_FOOD_DISTRIBUTION);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Food distribution is scheduled");
    }

    public ResponseEntt startPreparingForTheFlightDeparture(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightsEntity = this.getFlightById(flightId);
        if(!flightsEntity.getStatus().equals(FlightState.SOLD)) {
            throw new StateException(
                    "Before initiating the departure of the flight, all tickets must be redeemed for the flight!"
            );
        }

        flightsEntity.setStatus(FlightState.DEPARTURE_INITIATED);
        this.flightRepository.save(flightsEntity);

        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody(
                        "Flight dispatch initiated"
                );
    }

    public ResponseEntt requestToLandTheAircraft(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.FLIGHT_STARTED)){
            throw new StateException("To board, all passengers must take their seats");
        }
        flightEntity.setStatus(FlightState.LANDING_REQUESTED);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Landing request");
    }

    public ResponseEntt assignLanding(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.LANDING_REQUESTED)){
            throw new StateException("Landing must be requested by the pilot");
        }
        flightEntity.setStatus(FlightState.LANDING_PENDING_CONFIRMATION);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Landing ASSIGN");
    }

    public ResponseEntt confirmationOfTheLandingOfAircraft(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.LANDING_PENDING_CONFIRMATION)){
            throw new StateException("Landing must be assigned as a dispatcher");
        }
        flightEntity.setStatus(FlightState.LANDING_CONFIRMED);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Landing is allowed");
    }

    public ResponseEntt endFlight(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.LANDING_CONFIRMED)){
            throw new StateException("Landing must be assigned as a main dispatcher");
        }

        flightEntity.setStatus(FlightState.ARRIVED);
        flightEntity.getAircraftEntity().setStatus(AircraftState.INSPECTED);
        for (FlightUserEntity flightUserEntity : flightEntity.getUserFlightsEntities()){
            flightUserEntity.setUserStatus(UserFlightState.ARRIVED);
            if (flightUserEntity.getUsersEntity().getUserPosition().getPositionTitle().equals("CLIENT")){
                flightUserEntity.getAircraftSeatsEntity().setReserved(Boolean.FALSE);
            }
        }
          this.flightRepository.save(flightEntity);

        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody("Flight end");
    }

    public List<FlightResponseDto> allFlights(FlightState flightState) throws FlightNotFoundException {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.predicateForFlightSearch(flightState)
        );
        Iterable<FlightEntity> flightEntityIterable =
                this.flightRepository.findAll(booleanBuilder.getValue());
        List<FlightResponseDto> flightResponseDtoList =
                StreamSupport
                        .stream(flightEntityIterable.spliterator(), false)
                        .map(FlightMapper::mapToFlightResponseDto)
                        .collect(Collectors.toList());
        if(flightResponseDtoList.isEmpty()) {
            throw new FlightNotFoundException("Flight not found");
        }
        return flightResponseDtoList;

    }

    public ResponseEntt initiateDeparture(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.getFlightById(flightId);

        flightEntity.setStatus(FlightState.DEPARTURE_READY);
        this.flightRepository.save(flightEntity);
        return new  ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody("Flight start initiated");
    }

    public Predicate predicateForFlightSearch(FlightState flightState){
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QFlightEntity root = QFlightEntity.flightEntity;

        if(Objects.nonNull(flightState)) {
            booleanBuilder.and(root.status.eq(flightState));
        }
        return booleanBuilder.getValue();
    }
}
