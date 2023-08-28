package com.example.airportv3.repository;

import com.example.airportv3.entity.ClientReviewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientReviewsRepository extends JpaRepository<ClientReviewsEntity,Long> {
}
