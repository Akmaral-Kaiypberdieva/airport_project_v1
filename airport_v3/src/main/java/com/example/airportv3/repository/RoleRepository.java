package com.example.airportv3.repository;

import com.example.airportv3.entity.PositionEntity;
import com.example.airportv3.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long> {
    List<RoleEntity> getRoleEntitiesByUserPositions(PositionEntity positionEntity);
}
