package com.example.airportv3.controller;

import com.example.airportv3.dto.UserRequestDto;
import com.example.airportv3.dto.UserResponseDto;
import com.example.airportv3.service.GetAllFreeMembersService;
import com.example.airportv3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {
    private final UserService userService;

    @PreAuthorize(value = "hasAnyRole('CLIENT')")
    @PutMapping("update")
    public UserResponseDto updateThisUserInfo(
            @RequestBody UserRequestDto userRequestDto){
        return this.userService.updateThisUserInfo(userRequestDto);
    }


    @PreAuthorize(value = "hasAnyRole('CLIENT')")
    @DeleteMapping("/delete_account")
     public UserResponseDto removeAccount(){
       return this.userService.removeAccount();
    }


}
