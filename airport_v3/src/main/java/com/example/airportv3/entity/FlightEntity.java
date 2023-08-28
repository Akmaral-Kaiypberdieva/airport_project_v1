package com.example.airportv3.entity;

import com.example.airportv3.entity.enums.FlightState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "destination")
    private String destination;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "flight_status")
    private FlightState status;
    @Column(name = "tickets_left")
    private Integer ticketsLeft;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftEntity aircraftEntity;
    @OneToMany(mappedBy = "flightsEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<FlightUserEntity> userFlightsEntities;
    @OneToMany(mappedBy = "flightsEntity")
    private List<ClientReviewsEntity> clientReviewsEntities;

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}
