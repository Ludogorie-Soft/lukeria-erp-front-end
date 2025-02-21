package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.CartonDTO;
import com.example.LukeriaFrontendApplication.dtos.ManufacturedProductDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-carton", url = "${backend.base-url}/carton")
public interface CartonClient {

    @GetMapping()
    List<CartonDTO> getAllCartons(@RequestHeader("Authorization") String auth);

    @GetMapping("/{id}")
    CartonDTO getCartonById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String auth);

    @PostMapping
    CartonDTO createCarton(@Valid @RequestBody CartonDTO cartonDTO, @RequestHeader("Authorization") String auth);

    @PutMapping("/{id}")
    void updateCarton(@PathVariable("id") Long id, @Valid @RequestBody CartonDTO cartonDTO, @RequestHeader("Authorization") String auth);

    @DeleteMapping("/{id}")
    String deleteCartonById(@PathVariable("id") Long id, @RequestHeader("Authorization") String auth);

    @GetMapping("/manufactured-products")
    List<ManufacturedProductDTO> getAllManufacturedProducts(@RequestHeader("Authorization") String auth);


}
