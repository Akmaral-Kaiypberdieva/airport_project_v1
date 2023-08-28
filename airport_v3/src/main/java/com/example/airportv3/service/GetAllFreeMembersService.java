package com.example.airportv3.service;

import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.entity.QUserEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.mapper.UserMapper;
import com.example.airportv3.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.querydsl.core.types.Predicate;

@Service
@RequiredArgsConstructor
public class GetAllFreeMembersService {
    // продолжить писать после реализаций UserService
    private final UserRepository userRepository;
    private BooleanBuilder booleanBuilder = new BooleanBuilder();

    public List<UserResponseDto> getFreeEngineers() throws UserNotFoundException {

              booleanBuilder.and(
                      this.predicateForSearchUserRoleSearch(List.of("ENGINEER"))
              );

              Iterable<UserEntity> userEntityIterable =
                      this.userRepository.findAll(booleanBuilder.getValue());

              List<UserEntity> userEntities =
                      StreamSupport
                              .stream(userEntityIterable.spliterator(),false)
                              .collect(Collectors.toList());
              userEntities.removeIf(u -> Objects.nonNull(u.getServicedAircraft()));

              if (userEntities.isEmpty()){
                  throw new UserNotFoundException("There are no available engineers yet");
              }
              return userEntities.stream()
                      .map(UserMapper::mapToUserResponseDto)
                      .collect(Collectors.toList());
    }

    public List<UserResponseDto> getAllEmployees(List<String> userPositions,Boolean isEnabled) throws UserNotFoundException {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                this.predicateForSearchUser(isEnabled)
        );

        booleanBuilder.and(this.predicateForSearchUserPosition(userPositions));
        Iterable<UserEntity> userEntityIterable =
                this.userRepository.findAll(booleanBuilder.getValue());
        List<UserResponseDto> userResponseDtoList =
                StreamSupport.stream(userEntityIterable.spliterator(),false)
                        .map(UserMapper::mapToUserResponseDto)
                        .collect(Collectors.toList());

        if (userResponseDtoList.isEmpty()){
            throw new UserNotFoundException("No employees were found according to the specified position!");
        }
        return userResponseDtoList;
    }

    public Predicate predicateForSearchUser(
            Boolean isEnabled
    ){
        QUserEntity root = QUserEntity.userEntity;

        if (Objects.nonNull(isEnabled)){
            booleanBuilder.and(root.isEnabled.eq(isEnabled));
        }
        return booleanBuilder.getValue();
    }

    public Predicate predicateForSearchUserPosition(List<String> employeesPositions){
        QUserEntity root = QUserEntity.userEntity;
        booleanBuilder.and(root.userPosition.positionTitle.ne("CLIENT"));
        if (Objects.nonNull(employeesPositions) && !employeesPositions.isEmpty()){
            booleanBuilder.and(root.userPosition.positionTitle.in(employeesPositions));
        }
        return booleanBuilder.getValue();
    }

    public Predicate predicateForSearchUserRoleSearch(List<String> roleName){
        QUserEntity root = QUserEntity.userEntity;

        if ((!roleName.isEmpty())){
            booleanBuilder.and(root.roles.any().roleTitle.in(roleName));
        }
        return booleanBuilder.getValue();
    }

    public UserEntity getEngineerEntityById(Long engineerId) throws UserNotFoundException {
        if(Objects.isNull(engineerId)) {
            throw new IllegalArgumentException("Id can't be null!");
        }
        if(engineerId < 1L) {
            throw new IllegalArgumentException("The engineer's id cannot be less than 1!");
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QUserEntity root = QUserEntity.userEntity;

        booleanBuilder.and(root.roles.any().roleTitle.eq("ENGINEER"));
        booleanBuilder.and(root.id.eq(engineerId));

        Optional<UserEntity> userEntityOptional =
                this.userRepository.findOne(booleanBuilder.getValue());
        if(userEntityOptional.isEmpty()) {
            throw new UserNotFoundException(
                    String.format("No engineer with id %d found!", engineerId)
            );
        }
        return userEntityOptional.get();
    }

}
// подумать надо ли вообще это


//    public List<UserResponseDto> getAllClients() throws UserNotFoundException {
//
////        QUserEntity root = QUserEntity.userEntity;
////
////        booleanBuilder.and(root.userPosition.positionTitle.eq("CLIENT"));
////
////        Iterable<UserEntity> userEntityIterable =
////                this.userRepository.findAll(booleanBuilder.getValue());
////        List<UserResponseDto> userResponseDtoList =
////                StreamSupport
////                        .stream(userEntityIterable.spliterator(), false)
////                        .map(UserMapper::mapToUserResponseDto)
////                        .collect(Collectors.toList());
////
////        if (userResponseDtoList.isEmpty()){
////            throw new UserNotFoundException("User not found");
////        }
////        return userResponseDtoList;
//    }
