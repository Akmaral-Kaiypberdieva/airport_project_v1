package com.example.airportv3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "client_reviews")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientReviewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "feedback_text")
    private String feedbackText;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private UserEntity usersEntity;
    @ManyToOne
    @JoinColumn(name = "flight_id", referencedColumnName = "id")
    private FlightEntity flightsEntity;

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

}
