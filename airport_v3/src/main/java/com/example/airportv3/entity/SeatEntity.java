package com.example.airportv3.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "seats_in_aircraft")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "number_in_row")
    private Integer numberInRow;
    @Column(name = "row_number")
    private Integer rowNumber;
    @Column(name = "is_reserved")
    private Boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", referencedColumnName = "id")
    private AircraftEntity aircraftEntity;
    @OneToMany(mappedBy = "aircraftSeatsEntity")
    private List<FlightUserEntity> userFlightsEntities;

    public SeatEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public SeatEntity setNumberInRow(Integer numberInRow) {
        this.numberInRow = numberInRow;
        return this;
    }

    public SeatEntity setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
        return this;
    }

    public SeatEntity setReserved(Boolean reserved) {
        isReserved = reserved;
        return this;
    }

    public SeatEntity setAircraftEntity(AircraftEntity aircraftEntity) {
        this.aircraftEntity = aircraftEntity;
        return this;
    }

    public SeatEntity setUserFlightsEntities(List<FlightUserEntity> userFlightsEntities) {
        this.userFlightsEntities = userFlightsEntities;
        return this;
    }
}
