package com.example.airportv3.service;

import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.FlightEntity;
import com.example.airportv3.entity.enums.FlightState;
import com.example.airportv3.exception.FlightNotFoundException;
import com.example.airportv3.exception.StateException;
import com.example.airportv3.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightCrewReadyService {
    private final FlightService flightService;
    private final FlightRepository flightRepository;
    public void messageThatAllCrewMembersAreReadyToFly(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.CLIENTS_READY)){
            throw new StateException("Before checking the readiness of crew members, it is necessary to check the readiness of passengers");
        }
        flightEntity.setStatus(FlightState.CREW_READY);
        this.flightRepository.save(flightEntity);
    }

    public ResponseEntt confirmationOfCheckinsForFlight(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);

        if (!flightEntity.getStatus().equals(FlightState.CREW_MEMBERS_REGISTERED)){
            throw new StateException("Register all crew members");
        }
        flightEntity.setStatus(FlightState.SELLING_TICKETS);
        this.flightRepository.save(flightEntity);

        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody("Flight registration is confirmed!");
    }
    public void checkedReadyClientsBeforeFlight(Long flightId) throws StateException, FlightNotFoundException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.CLIENT_CHECK)){
            throw new StateException("Checking passengers a must be assigned by the dispatcher");
        }
        flightEntity.setStatus(FlightState.CLIENT_CHECK);
        this.flightRepository.save(flightEntity);
    }

    public ResponseEntt confirmClientsReadiness(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightsEntity = this.flightService.getFlightById(flightId);


        flightsEntity.setStatus(FlightState.CLIENTS_READY);
        this.flightRepository.save(flightsEntity);

        return new ResponseEntt()
                .setStatus(HttpStatus.OK)
                .setBody("Clients is ready");
    }

    public void allClientsHavePassedTheInstructions(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.CLIENTS_BRIEFED)){
            throw new StateException("Checking passengers a must be assigned by the main seward");
        }
        flightEntity.setStatus(FlightState.CLIENTS_BRIEFED);
        this.flightRepository.save(flightEntity);
    }


    public void foodDistribution(Long flightId) throws FlightNotFoundException, StateException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.FLIGHT_FOOD_DISTRIBUTION)){
            throw new StateException("Checking passengers a must be assigned by the main seward");
        }
        flightEntity.setStatus(FlightState.FLIGHT_FOOD_DISTRIBUTED);
        this.flightRepository.save(flightEntity);
    }


    public ResponseEntt crewReadiness(Long flightId) throws StateException, FlightNotFoundException {
        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
        if (!flightEntity.getStatus().equals(FlightState.TECH_PREP_COMPLETE)){
            throw new StateException("The refueling of the aircraft was not carried out");
        }
        flightEntity.setStatus(FlightState.FLIGHT_FOOD_DISTRIBUTED);
        this.flightRepository.save(flightEntity);
        return new ResponseEntt<>()
                .setStatus(HttpStatus.OK)
                .setBody("Customer readiness check started");
    }

//    public ResponseEntt confirmFlightRegistration(Long flightId){
//        FlightEntity flightEntity = this.flightService.getFlightById(flightId);
//
//    }

}
