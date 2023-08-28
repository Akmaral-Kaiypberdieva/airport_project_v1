package com.example.airportv3.service;

import com.example.airportv3.entity.UserEntity;
import com.example.airportv3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(Objects.isNull(username) || username.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name can't be null or empty"
            );
        }
        Optional<UserEntity> usersEntityOptional
                = this.userRepository.getUserEntityByUsername(username);
        if(usersEntityOptional.isEmpty()){
            throw new UsernameNotFoundException(
                    "User not found!"
            );
        }
        UserEntity usersEntity = usersEntityOptional.get();
        return usersEntity;
    }
}

