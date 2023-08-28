package com.example.airportv3.service;

import com.example.airportv3.dto.SeatResponseDto;
import com.example.airportv3.entity.QSeatEntity;
import com.example.airportv3.entity.SeatEntity;
import com.example.airportv3.exception.SeatNotFoundException;
import com.example.airportv3.exception.SeatReservedException;
import com.example.airportv3.mapper.AircraftMapper;
import com.example.airportv3.repository.SeatRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    public List<SeatResponseDto> getAllAircraftSeats(Long aircraftId,Boolean isReserved) throws SeatNotFoundException {
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("The plane id for finding seats for booking cannot be null!");
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSeatEntity root = QSeatEntity.seatEntity;

        booleanBuilder.and(root.aircraftEntity.id.eq(aircraftId));
        if (Objects.nonNull(isReserved)){
            booleanBuilder.and(root.isReserved.eq(isReserved));
        }

        Iterable<SeatEntity> seatEntityIterable =
                this.seatRepository.findAll(booleanBuilder.getValue());

        List<SeatResponseDto> seatResponseDtoList =
                StreamSupport
                        .stream(seatEntityIterable.spliterator(),false)
                        .map(AircraftMapper::mapToSeatResponseDto)
                        .collect(Collectors.toList());

        if (seatResponseDtoList.isEmpty()){
            throw new SeatNotFoundException("No places for booking according to the specified parameters were found");
        }
        return seatResponseDtoList;
    }
    public List<SeatEntity> generateSeatInAircraft(
            Integer rowsNumber,
            Integer numberOfSeatInRow
    ) {
        if (rowsNumber == null || rowsNumber <= 0 || numberOfSeatInRow == null || numberOfSeatInRow <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        List<SeatEntity> seatEntities = new ArrayList<>();
        for (int rowNumber = 1; rowNumber <= rowsNumber; rowNumber++) {
            for (int numberInRow = 1; numberInRow <= numberOfSeatInRow; numberInRow++) {
                seatEntities.add(
                        new SeatEntity()
                                .setRowNumber(rowNumber)
                                .setNumberInRow(numberInRow)
                                .setReserved(Boolean.FALSE)
                );

            }
        }
        return seatEntities;
    }

    public SeatEntity reserveSeat(Long seatId) throws SeatNotFoundException, SeatReservedException {
        SeatEntity seat = this.seatById(seatId);

        if (seat.getIsReserved()){
            throw new SeatReservedException("The place with ID "+ seat.getId() +" has already been booked");
        }
        seat.setReserved(Boolean.TRUE);
        return this.seatRepository.save(seat);
    }

    public SeatEntity cancellationOfSeatReservation(Long seatId) throws SeatNotFoundException, SeatReservedException {
        SeatEntity seat = this.seatById(seatId);

        if (!seat.getIsReserved()){
            throw new SeatReservedException("The place with ID "+ seat.getId() +" has already been free");
        }
        seat.setReserved(Boolean.FALSE);
        return this.seatRepository.save(seat);
    }

    public SeatEntity seatById(Long seatId) throws SeatNotFoundException {
        if(Objects.isNull(seatId)) {
            throw new IllegalArgumentException("Seat id cant be null!");
        }

        Optional<SeatEntity> aircraftSeatsEntityOptional =
                this.seatRepository.getAircraftSeatsEntityById(seatId);
        if(aircraftSeatsEntityOptional.isEmpty()) {
            throw new SeatNotFoundException(
                    String.format("Seat in aircraft %d not found!", seatId)
            );
        }
        return aircraftSeatsEntityOptional.get();
    }

    public Integer getFreeSeatsInAircraftById(Long aircraftId){
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("Aircraft id can't be null");
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSeatEntity root = QSeatEntity.seatEntity;

        booleanBuilder.and(root.aircraftEntity.id.eq(aircraftId));
        booleanBuilder.and(root.isReserved.eq(Boolean.FALSE));

        Iterable<SeatEntity> aircraftSeatsEntityIterable =
                this.seatRepository.findAll(booleanBuilder.getValue());
        List<SeatEntity> aircraftSeatsEntities =
                StreamSupport
                        .stream(aircraftSeatsEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        return aircraftSeatsEntities.size();
    }

    public Integer numberOfFreeSeatsByAircraftId(Long aircraftId){
        if(Objects.isNull(aircraftId)) {
            throw new IllegalArgumentException("Aircraft id can't be null!");
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QSeatEntity root = QSeatEntity.seatEntity;

        booleanBuilder.and(root.aircraftEntity.id.eq(aircraftId));
        booleanBuilder.and(root.isReserved.eq(Boolean.FALSE));

        Iterable<SeatEntity> aircraftSeatsEntityIterable =
                this.seatRepository.findAll(booleanBuilder.getValue());
        List<SeatEntity> aircraftSeatsEntities =
                StreamSupport
                        .stream(aircraftSeatsEntityIterable.spliterator(), false)
                        .collect(Collectors.toList());

        return aircraftSeatsEntities.size();
    }
}
