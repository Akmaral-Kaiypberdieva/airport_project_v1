package com.example.airportv3.repository;

import com.example.airportv3.entity.InspectionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionsRepository extends JpaRepository<InspectionsEntity,Long>,
        QuerydslPredicateExecutor<InspectionsEntity> {
    @Query(value = "SELECT MAX (inspection_code) FROM public.inspections",nativeQuery = true)
    Long getCurrentMaxInspectionsCode();

    @Query(
            value = "with aircraft_inspections as (select * from inspections where aircraft_id = :aircraftId" +
                    "select * from aircraft_inspections" +
                    "where inspection_code = (select max(inspection_code) from aircraft_inspections);",
            nativeQuery = true)
    List<InspectionsEntity> getLastAircraftInspectionByAircraftId(
            @Param(value = "aircraftId") Long aircraftId
    );
}
