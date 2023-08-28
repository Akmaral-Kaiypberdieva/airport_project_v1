package com.example.airportv3.repository;


import com.example.airportv3.entity.FlightUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightUserRepository extends JpaRepository<FlightUserEntity, Long>
,QuerydslPredicateExecutor<FlightUserEntity>{
    Optional<FlightUserEntity> getFlightUserEntityById(Long flightUserId);
//    Optional<FlightUserEntity> getFlightUserEntityByUsersEntityId(Long userId);
}
