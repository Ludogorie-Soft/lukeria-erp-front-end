package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-plate", url = "http://localhost:8088/api/v1/plate")
public interface PlateClient {
    @GetMapping()
    List<PlateDTO> getAllPlates();

    @GetMapping("/{id}")
    PlateDTO getPlateById(@PathVariable(name = "id") Long id);

    @PostMapping
    PlateDTO createPlate(@Valid @RequestBody PlateDTO plateDTO);

    @PutMapping("/{id}")
    void updatePlate(@PathVariable("id") Long id, @Valid @RequestBody PlateDTO plateDTO) ;

    @DeleteMapping("/{id}")
    String deletePlateById(@PathVariable("id") Long id);

}
