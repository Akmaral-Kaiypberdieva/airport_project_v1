package com.example.airportv3.service;

import com.example.airportv3.dto.PositionDto;
import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.entity.PositionEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.exception.PositionNotExistsException;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.mapper.UserMapper;
import com.example.airportv3.repository.PositionRepository;
import com.example.airportv3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
   private final UserRepository userRepository;
   private final PositionRepository positionRepository;
   private final PasswordEncoder passwordEncoder;

   public UserResponseDto removeAccount(){
       UserEntity user =
               (UserEntity) SecurityContextHolder.getContext().getAuthentication();
       user.setIsEnabled(false);
       return UserMapper.mapToUserResponseDto(
               this.userRepository.save(user)
       );

   }
       public UserResponseDto updateThisUserInfo(UserRequestDto userRequestDto){
           UserEntity user =
                   (UserEntity) SecurityContextHolder.getContext().getAuthentication();

           user.setUsername(userRequestDto.getUsername());
           user.setPassword(this.passwordEncoder.encode(userRequestDto.getPassword()));
           user.setEmail(userRequestDto.getEmail());

           user = this.userRepository.save(user);
           return UserMapper.mapToUserResponseDto(user);
       }

       public UserResponseDto updateUserInfo(UserRequestDto userRequestDto, Long userid) throws UserNotFoundException, PositionNotExistsException {
           if(Objects.isNull(userid)) {
               throw new IllegalArgumentException("The user ID cannot be null!");
           }
           Optional<UserEntity> userEntityOptional =
                   this.userRepository.getUserEntityById(userid);
           if (userEntityOptional.isEmpty()){
               throw new UserNotFoundException(
                       String.format("User with ID %d not found!",userid));
           }
           UserEntity user = userEntityOptional.get();

           Long positionId = userRequestDto.getPositionId();
           Optional<PositionEntity> positionEntity = this.positionRepository.getPositionEntityById(positionId);

           if (positionEntity.isEmpty()){
               throw new PositionNotExistsException(
                       String.format("The position of the user with ID %d does not exist in the system!", positionId)
               );
           }

           user.setUserPosition(positionEntity.get());
           user.setUsername(userRequestDto.getUsername());
           user.setPassword(this.passwordEncoder.encode(userRequestDto.getPassword()));

           this.userRepository.save(user);
           return UserMapper.mapToUserResponseDto(user);
       }

    public List<PositionDto> allEmployeePositions(){
        List<PositionEntity> positionEntities =
                this.positionRepository.getPositionEntitiesByPositionTitle("CLIENT");
        return UserMapper.mapToPositionDtoList(positionEntities);
    }

    public List<UserEntity> userEntitiesByIdList(List<Long> userIdList) throws UserNotFoundException {
        if(Objects.isNull(userIdList) || userIdList.isEmpty()) {
            throw new IllegalArgumentException("Id can't be null or empty");
        }

        List<UserEntity> userEntityList =
                this.userRepository.getUserEntitiesByIdIn(userIdList);

        if(userEntityList.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return userEntityList;
    }

}
