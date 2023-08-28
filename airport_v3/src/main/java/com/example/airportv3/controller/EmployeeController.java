package com.example.airportv3.controller;

import com.example.airportv3.dto.PositionDto;
import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.exception.UserNotFoundException;
import com.example.airportv3.service.GetAllFreeMembersService;
import com.example.airportv3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final UserService userService;
    private final GetAllFreeMembersService getAllFreeMembersService;

    @PreAuthorize(value = "hasRole('MANAGER')")
    @DeleteMapping("/delete_account")
    public UserResponseDto removeAccount(){
        return this.userService.removeAccount();
    }
    @PreAuthorize(value = "hasRole('MANAGER')")
    @PutMapping("/update_info")
    public UserResponseDto updateThisUserInfo(
            @RequestBody UserRequestDto userRequestDto){
        return this.userService.updateThisUserInfo(userRequestDto);
    }

    @PreAuthorize(value = "hasRole('MANAGER')")
    @GetMapping("/get_all")
    public List<UserResponseDto> getAllEmployees(
            @RequestParam(required = false) List<String> userPositions,
            @RequestParam(required = false) Boolean isEnabled) throws UserNotFoundException{
        return this.getAllFreeMembersService.getAllEmployees(userPositions,isEnabled);
    }

    @PreAuthorize(value = "hasAnyRole('MANAGER', 'MAIN_ENGINEER')")
    @GetMapping(value = "/free_engineers/")
    public List<UserResponseDto> getFreeEngineers() throws UserNotFoundException{
        return this.getAllFreeMembersService.getFreeEngineers();
    }
    @PreAuthorize(value = "hasRole('MANAGER')")
    @GetMapping("/position")
    public List<PositionDto> allEmployeePositions(){
        return this.userService.allEmployeePositions();
    }
}
