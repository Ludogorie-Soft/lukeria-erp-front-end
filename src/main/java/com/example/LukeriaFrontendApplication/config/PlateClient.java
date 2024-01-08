package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.PlateDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-plate", url = "http://localhost:8088/api/v1/plate")
public interface PlateClient {
    @GetMapping()
    List<PlateDTO> getAllPlates(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    PlateDTO getPlateById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    PlateDTO createPlate(@Valid @RequestBody PlateDTO plateDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updatePlate(@PathVariable("id") Long id, @Valid @RequestBody PlateDTO plateDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deletePlateById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

}
