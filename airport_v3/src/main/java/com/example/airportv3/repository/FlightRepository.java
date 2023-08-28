package com.example.airportv3.repository;

import com.example.airportv3.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity,Long> ,
        QuerydslPredicateExecutor<FlightEntity> {
    Optional<FlightEntity> getFlightsEntityById(Long flightId);
}
