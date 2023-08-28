package com.example.airportv3.controller;

import com.example.airportv3.dto.JwtResponse;
import com.example.airportv3.dto.UserLoginDto;
import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.exception.PositionNotExistsException;
import com.example.airportv3.exception.RoleForeUserNotAppropriateException;
import com.example.airportv3.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee_auth")
@RequiredArgsConstructor
public class EmployeeAuthController {
    private final UserAuthService userAuthService;
    @PreAuthorize(value = "hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/register")
    public UserResponseDto registerEmployee(
            @RequestBody UserRequestDto userRequestDto) throws RoleForeUserNotAppropriateException, PositionNotExistsException {
        return userAuthService.registerEmployee(userRequestDto);
    }

    @GetMapping("/login")
    public JwtResponse login(
            @RequestBody UserLoginDto userLoginDto){
        return userAuthService.loginEmployee(userLoginDto);
    }
}
