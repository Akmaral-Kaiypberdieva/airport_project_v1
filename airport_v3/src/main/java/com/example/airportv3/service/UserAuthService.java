package com.example.airportv3.service;

import com.example.airportv3.dto.JwtResponse;
import com.example.airportv3.dto.UserLoginDto;
import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponseDto registerEmployee(UserRequestDto userRequestDto) throws PositionNotExistsException, RoleForeUserNotAppropriateException {
        UserEntity user =
                UserMapper.mapUserToDtoEntity(userRequestDto);

        Optional<PositionEntity> positionEntityOptional =
                this.positionRepository.getPositionEntityById(userRequestDto.getPositionId());
        if (positionEntityOptional.isEmpty()){
            throw new PositionNotExistsException("Such a position does not exist");
        }

        PositionEntity positionEntity = positionEntityOptional.get();
        user.setUserPosition(positionEntity);

        List<RoleEntity> roleEntities =
                this.roleRepository.getRoleEntitiesByUserPositions(positionEntity);
        if (roleEntities.isEmpty()){
            throw new RoleForeUserNotAppropriateException("No roles are set for the position of user " + positionEntity.getPositionTitle());
        }
        user.setRoles(roleEntities);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        return UserMapper.mapToUserResponseDto(user);
    }

    public JwtResponse loginEmployee(UserLoginDto userLoginDto) {

        UserDetails authenticatedUser = this.userDetailsService.loadUserByUsername(userLoginDto.getEmail());
        if (this.passwordEncoder.matches(userLoginDto.getPassword(), authenticatedUser.getPassword())) {
            Authentication authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            authenticatedUser,
                            null,
                            authenticatedUser.getAuthorities()
                    );
            return AuthMapper.mapToResponseDto(this.jwtUtils.generateToken(authentication));
        }
        return AuthMapper.mapToResponseDto("Incorrect Password or Username");
    }


}