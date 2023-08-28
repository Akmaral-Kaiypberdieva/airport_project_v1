package com.example.airportv3.repository;

import com.example.airportv3.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>,
        QuerydslPredicateExecutor<UserEntity> {
    Optional<UserEntity> getUserEntityByUsername(String username);
    Optional<UserEntity> getUserEntityById(Long userId);
    List<UserEntity> getUserEntitiesByIdIn(List<Long> userIdList);
}
