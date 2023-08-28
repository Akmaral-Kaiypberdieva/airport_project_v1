package com.example.airportv3.entity;

import com.example.airportv3.entity.enums.UserFlightState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_flights")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FlightUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "user_status")
    private UserFlightState userStatus;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity usersEntity;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "seat_id", referencedColumnName = "id")
    private SeatEntity aircraftSeatsEntity;
    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "flight_id", referencedColumnName = "id")
    private FlightEntity flightsEntity;

    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }
}
