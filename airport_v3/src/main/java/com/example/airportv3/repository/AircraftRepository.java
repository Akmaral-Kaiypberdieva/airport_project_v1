package com.example.airportv3.repository;

import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftRepository
        extends JpaRepository<AircraftEntity,Long> {
    Optional<AircraftEntity> getAircraftEntityById(Long seatId);
}
