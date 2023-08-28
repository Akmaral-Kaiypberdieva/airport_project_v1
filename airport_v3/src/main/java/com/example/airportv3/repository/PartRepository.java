package com.example.airportv3.repository;

import com.example.airportv3.entity.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<PartEntity,Long>  {
    List<PartEntity> getPartEntitiesByIdIn(List<Long> partsId);
}
