package com.example.LukeriaFrontendApplication.config;

import com.example.LukeriaFrontendApplication.dtos.PackageDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "lukeria-erp-package", url = "http://localhost:8088/api/v1/package")

public interface PackageClient {
    @GetMapping
    List<PackageDTO> getAllPackages() ;

    @GetMapping("/{id}")
    PackageDTO getPackageById(@PathVariable(name = "id") Long id);

    @PostMapping
    PackageDTO createPackage(@Valid @RequestBody PackageDTO packageDTO) ;

    @PutMapping("/{id}")
    void updatePackage(@PathVariable("id") Long id, @RequestBody PackageDTO packageDTO);

    @DeleteMapping("/{id}")
   String deletePackageById(@PathVariable("id") Long id);
}
