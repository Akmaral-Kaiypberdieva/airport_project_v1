package com.example.airportv3.service;

import com.example.airportv3.dto.*;
import com.example.airportv3.entity.PositionEntity;
import com.example.airportv3.entity.RoleEntity;
import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.exception.PositionNotExistsException;
import com.example.airportv3.exception.RoleForeUserNotAppropriateException;
import com.example.airportv3.mapper.AuthMapper;
import com.example.airportv3.mapper.UserMapper;
import com.example.airportv3.repository.PositionRepository;
import com.example.airportv3.repository.RoleRepository;
import com.example.airportv3.repository.UserRepository;
import com.example.airportv3.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

   public UserResponseDto registerClient(UserRequestDto userRequestDto) throws PositionNotExistsException, RoleForeUserNotAppropriateException {
       UserEntity userEntity =
               UserMapper.mapUserToDtoEntity(userRequestDto);

       Optional<PositionEntity> positionEntityOptional =
               this.positionRepository.getPositionEntityByPositionTitle("CLIENT");

       if (positionEntityOptional.isEmpty()){
           throw new PositionNotExistsException("Such a position does not exist");
       }

       PositionEntity positionEntity = positionEntityOptional.get();
       userEntity.setUserPosition(positionEntity);

//       List<RoleEntity> userRoleList =
//               this.roleRepository.getRoleEntitiesByUserPositions(positionEntity);
//       if (userRoleList.isEmpty()){
//           throw new RoleForeUserNotAppropriateException("No roles are set for the position of user " + positionEntity.getPositionTitle());
//       }

//       userEntity.setRoles(userRoleList);
       userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

       userEntity = this.userRepository.save(userEntity);
       return UserMapper.mapToUserResponseDto(userEntity);
   }

   public JwtResponse loginClient(UserLoginDto userLoginDto){

       UserDetails authenticatedUser = this.userDetailsService.loadUserByUsername(userLoginDto.getEmail());
       if(this.passwordEncoder.matches(userLoginDto.getPassword(), authenticatedUser.getPassword())){
           Authentication authentication =
                   UsernamePasswordAuthenticationToken.authenticated(
                           authenticatedUser,
                           null,
                           authenticatedUser.getAuthorities()
                   );
           return AuthMapper.mapToResponseDto(this.jwtUtils.generateToken(authentication));
       }
       return AuthMapper.mapToResponseDto("Incorrect Password or Username");


//       SecurityContextHolder.getContext().setAuthentication(authentication);
//       String jwt = jwtUtils.generateToken(authentication);
//       UserEntity user = (UserEntity) this.userDetailsService.loadUserByUsername(userLoginDto.getEmail());
//       List<String> roles = user.getAuthorities().stream()
//               .map(item -> item.getAuthority())
//               .collect(Collectors.toList());
//       return ResponseEntity.ok(new JwtResponse(jwt,user.getId(),user.getUsername(),user.getEmail(),roles));
   }
}
