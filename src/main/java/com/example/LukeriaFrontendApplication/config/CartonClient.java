package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-carton", url = "http://localhost:8088/api/v1/carton")
public interface CartonClient {

    @GetMapping()
    List<CartonDTO> getAllCartons();

    @GetMapping("/{id}")
    CartonDTO getCartonById(@PathVariable(name = "id") Long id);

    @PostMapping
    CartonDTO createCarton(@Valid @RequestBody CartonDTO cartonDTO);

    @PutMapping("/{id}")
    void updateCarton(@PathVariable("id") Long id, @Valid @RequestBody CartonDTO cartonDTO) ;

    @DeleteMapping("/{id}")
     String deleteCartonById(@PathVariable("id") Long id);


}
