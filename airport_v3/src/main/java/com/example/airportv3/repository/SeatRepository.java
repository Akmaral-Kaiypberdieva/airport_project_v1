package com.example.airportv3.repository;

import com.example.airportv3.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long>,
        QuerydslPredicateExecutor<SeatEntity>
{
    Optional<SeatEntity> getAircraftSeatsEntityById(Long seatId);
}
