package com.example.airportv3.entity;

import com.example.airportv3.entity.enums.PartState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspections")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InspectionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "part_state")
    private PartState partState;
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    @Column(name = "inspection_code")
    private Long inspectionCode;

    @ManyToOne
    @JoinColumn(name = "conducted_by", referencedColumnName = "id")
    private UserEntity conductedBy;
    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private PartEntity partsEntity;
    @ManyToOne
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftEntity aircraftEntity;

    public InspectionsEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public InspectionsEntity setPartState(PartState partState) {
        this.partState = partState;
        return this;
    }

    public InspectionsEntity setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public InspectionsEntity setInspectionCode(Long inspectionCode) {
        this.inspectionCode = inspectionCode;
        return this;
    }

    public InspectionsEntity setConductedBy(UserEntity conductedBy) {
        this.conductedBy = conductedBy;
        return this;
    }

    public InspectionsEntity setPartsEntity(PartEntity partsEntity) {
        this.partsEntity = partsEntity;
        return this;
    }

    public InspectionsEntity setAircraftEntity(AircraftEntity aircraftEntity) {
        this.aircraftEntity = aircraftEntity;
        return this;
    }
}
