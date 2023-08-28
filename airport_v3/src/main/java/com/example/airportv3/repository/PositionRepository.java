package com.example.airportv3.repository;

import com.example.airportv3.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity,Long> {
  Optional<PositionEntity> getPositionEntityByPositionTitle(String positionTitle);
  List<PositionEntity> getPositionEntitiesByPositionTitle(String positionTitle);
  Optional<PositionEntity> getPositionEntityById(Long id);
}
