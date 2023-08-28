package com.example.airportv3.entity;

import com.example.airportv3.entity.enums.AircraftState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aircraft")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AircraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "aircraft_title")
    private String title;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "aircraft_status")
    private AircraftState status;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @OneToOne
    @JoinColumn(name = "serviced_by", referencedColumnName = "id")
    private UserEntity servicedBy;
    @OneToMany(mappedBy = "aircraftEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<SeatEntity> aircraftSeatsEntityList;
    @OneToMany(mappedBy = "aircraftEntity", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private List<InspectionsEntity> partInspectionsEntities;
    @OneToMany(mappedBy = "aircraftEntity")
    private List<FlightEntity> flightsEntities;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
            name = "aircraft_parts",
            joinColumns = @JoinColumn(name = "aircraft_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "part_id", referencedColumnName = "id")
    )
    private List<PartEntity> partsEntities;
    @PrePersist
    private void prePersist() {
        this.registeredAt = LocalDateTime.now();
    }

}
