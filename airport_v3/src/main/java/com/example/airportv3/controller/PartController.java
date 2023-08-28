package com.example.airportv3.controller;

import com.example.airportv3.dto.PartRequestDto;
import com.example.airportv3.dto.PartResponseDto;
import com.example.airportv3.dto.PartTypeResponseDto;
import com.example.airportv3.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/part")
@RequiredArgsConstructor
public class PartController {
    private final PartService partService;

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/register")
    public PartResponseDto registerPart(
            @RequestBody PartRequestDto partRequestDto){
        return this.partService.registerPart(partRequestDto);
    }
    @PreAuthorize(value = "hasAnyRole('DISPATCHER', 'MANAGER', 'ENGINEER', 'MAIN_ENGINEER', 'MAIN_DISPATCHER')")
    @GetMapping(value = "/part-types")
    public PartTypeResponseDto allPartTypes(){
        return this.partService.allPartTypes();
    }

    @PreAuthorize(value = "hasRole('DISPATCHER')")
    @PostMapping(value = "/all_register")
    public List<PartResponseDto> registerParts(List<PartRequestDto> partRequestDtoList){
        return this.partService.registerParts(partRequestDtoList);
    }
}
