package com.example.airportv3.service;

import com.example.airportv3.dto.InspectionRequestDto;
import com.example.airportv3.dto.InspectionResponseDto;
import com.example.airportv3.dto.ResponseEntt;
import com.example.airportv3.entity.AircraftEntity;
import com.example.airportv3.entity.InspectionsEntity;
import com.example.airportv3.entity.QInspectionsEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.entity.enums.AircraftState;
import com.example.airportv3.entity.enums.PartState;
import com.example.airportv3.exception.*;
import com.example.airportv3.mapper.InspectionMapper;
import com.example.airportv3.repository.AircraftRepository;
import com.example.airportv3.repository.InspectionsRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class AircraftInspectionService {
    private final InspectionsRepository inspectionsRepository;
    private final PartInspectionService partInspectionService;
    private final PartService partService;
    private final AircraftRepository aircraftRepository;
    private final AircraftService aircraftService;
    private final GetAllFreeMembersService getAllFreeMembersService;
  public String assignAnAircraftInspection(Long aircraftId, Long engineersId) throws AircraftNotFoundException, EngineerException, UserNotFoundException {
      AircraftEntity aircraft = this.aircraftService.findAircraftById(aircraftId);

      if(!aircraft.getStatus().equals(AircraftState.REGISTERED)) {
          throw new EngineerException(
                  "the inspection is carried out only after the transfer by the dispatcher"
          );
      }

      UserEntity engineer = this.getAllFreeMembersService.getEngineerEntityById(engineersId);
      if(Objects.nonNull(engineer.getServicedAircraft())) {
          throw new EngineerException("This engineer is busy" + engineer.getId());
      }

      engineer.setServicedAircraft(aircraft);
      aircraft.setServicedBy(engineer);

      aircraft.setStatus(AircraftState.ON_INSPECTION);

      aircraft = this.aircraftRepository.save(aircraft);
      return
                      String.format(
                              "The plane has been handed over to the engineer for inspection! Current status of the aircraft %s",
                              aircraft.getStatus().toString()
                      );
  }

  public ResponseEntt assignAircraftRepairs(Long aircraftId, Long engineersId) throws AircraftNotFoundException, EngineerException, InspectionNotFoundException, StateException, UserNotFoundException {
      AircraftEntity aircraft = this.aircraftService.findAircraftById(aircraftId);
      if(!aircraft.getStatus().equals(AircraftState.INSPECTED)) {
          throw new EngineerException(
                  "Чтобы отправить самолет на ремонт самолет должен быть осмотрен инженером!"
          );
      }
      if(!this.partService.getLastAircraftInspectionResult(aircraftId).equals(PartState.FIXING)) {
          throw new StateException(
                  String.format(
                          "Чтобы отправить самолет на ремонт хотя бы одна деталь самолета должны быть неисправна!" +
                                  "Результат последнего техосмотра: %s",
                          PartState.CORRECT
                  )
          );
      }

      UserEntity engineer = this.getAllFreeMembersService.getEngineerEntityById(engineersId);
      if(Objects.nonNull(engineer.getServicedAircraft())) {
          throw new EngineerException(
                  String.format(
                          "Невозможно назначить инженера с ID[%d] на ремонт самолета." +
                                  " В данный момент инженер обслуживает другой самолет!",
                          engineersId
                  )
          );
      }

      aircraft.setStatus(AircraftState.ON_REPAIRS);
      engineer.setServicedAircraft(aircraft);
      aircraft.setServicedBy(engineer);

      aircraft = this.aircraftRepository.save(aircraft);
      return new ResponseEntt()
              .setStatus(HttpStatus.OK)
              .setBody(
                      String.format(
                              "Самолет отправлен на ремонт! Текущий статус самолета:[%s]"
                              , aircraft.getStatus().toString()
                      )
              );
  }

  public List<InspectionResponseDto> inspectionAircraft(
          Long aircraftId,
          List<InspectionRequestDto> inspectionRequestDtoList
  ) throws AircraftNotFoundException, StateException, EngineerException, PartNotFoundException {
      if(inspectionRequestDtoList.isEmpty()) {
          throw new IllegalArgumentException("Список осмотров деталей не может быть null!");
      }

      AircraftEntity aircraft = this.aircraftService.findAircraftById(aircraftId);
      if(
              !aircraft.getStatus().equals(AircraftState.ON_INSPECTION) &&
                      !aircraft.getStatus().equals(AircraftState.ON_REPAIRS)
      ) {
          throw new StateException(
                  "Для проведения техосмотра самолета он должен быть назначен главным инжененром!"
          );
      }

      UserEntity engineer =
              (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if(!engineer.getId().equals(aircraft.getServicedBy().getId())) {
          throw new EngineerException(
                  String.format(
                          "Ошибка! Технический осмотр самолета с ID[%d] был назначен другому инженеру!",
                          aircraft.getId()
                  )
          );
      }

      List<InspectionResponseDto> partInspectionsResponseDtoList =
              this.partInspectionService.registerPartInspection(aircraft, inspectionRequestDtoList);

      aircraft.getServicedBy().setServicedAircraft(null);
      aircraft.setServicedBy(null);
      aircraft.setStatus(AircraftState.INSPECTED);

      this.aircraftRepository.save(aircraft);
      return partInspectionsResponseDtoList;
  }

  public List<InspectionResponseDto> partInspectionHistory(Long aircraftId, Long inspectionCode) throws PartInspectionNotFoundException {
      if (Objects.isNull(aircraftId)) {
          throw new IllegalArgumentException("ID самолета не может быть null!");
      }
      if (aircraftId < 1L) {
          throw new IllegalArgumentException("ID самолета не может быть меньше 1!");
      }

      BooleanBuilder booleanBuilder = new BooleanBuilder();
      QInspectionsEntity root = QInspectionsEntity.inspectionsEntity;

      booleanBuilder.and(root.aircraftEntity.id.eq(aircraftId));
      Iterable<InspectionsEntity> partInspectionsEntityIterable = null;
      if (Objects.nonNull(inspectionCode)) {
          if (inspectionCode < 1L) {
              throw new IllegalArgumentException("Код осмотра не может быть меньше 1!");
          }
          booleanBuilder.and(root.inspectionCode.eq(inspectionCode));

          partInspectionsEntityIterable = this.inspectionsRepository.findAll(booleanBuilder.getValue());
      }

      Comparator<InspectionsEntity> comparator = (o1, o2) -> o2.getInspectionCode().compareTo(o1.getInspectionCode());

      List<InspectionResponseDto> inspectionResponseDtoList =
              StreamSupport
                      .stream(partInspectionsEntityIterable.spliterator(), false)
                      .sorted(comparator)
                      .map(InspectionMapper::mapToInspectionResponseDto)
                      .collect(Collectors.toList());

      if (inspectionResponseDtoList.isEmpty()) {
          throw new PartInspectionNotFoundException(
                  String.format(
                          "Для самолета с ID[%d] по заданным параметрам не найдено ни одного техосмотра!",
                          aircraftId
                  )
          );
      }
      return inspectionResponseDtoList;
  }
}
