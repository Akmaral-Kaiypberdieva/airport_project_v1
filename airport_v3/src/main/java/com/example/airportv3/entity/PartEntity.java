package com.example.airportv3.entity;

import com.example.airportv3.entity.enums.PartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "parts_of_aircraft")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "part_title")
    private String title;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "part_type")
    private PartType partType;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToMany(mappedBy = "partsEntities")
    private List<AircraftEntity> aircraftsEntities;
    @OneToMany(mappedBy = "partsEntity")
    private List<InspectionsEntity> partInspectionsEntities;

}
