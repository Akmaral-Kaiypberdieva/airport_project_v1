package com.example.airportv3.controller;

import com.example.airportv3.dto.*;
import com.example.airportv3.exception.PositionNotExistsException;
import com.example.airportv3.exception.RoleForeUserNotAppropriateException;
import com.example.airportv3.service.ClientAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client_auth")
@RequiredArgsConstructor
public class ClientAuthController {
    private final ClientAuthService authService;
    @PostMapping("/register")
    public UserResponseDto registerClient(
            @RequestBody UserRequestDto userRequestDto) throws RoleForeUserNotAppropriateException,
            PositionNotExistsException {
        return authService.registerClient(userRequestDto);
    }

    @GetMapping("/login")
    public JwtResponse loginClient(
            @RequestBody UserLoginDto userLoginDto){
        return authService.loginClient(userLoginDto);
    }

}
